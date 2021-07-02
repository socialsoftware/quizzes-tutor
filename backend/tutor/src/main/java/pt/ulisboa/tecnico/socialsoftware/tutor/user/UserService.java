package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.events.DeleteAuthUserEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.USER_AGGREGATE_TYPE;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto createUser(String name, Role role, String username, boolean isActive, boolean isAdmin) {
        User user;
        if (username == null) {
            user = new User(name, role, isAdmin);
        }
        else {
            user = new User(name, username, role, isAdmin);
        }
        user.setActive(isActive);
        userRepository.save(user);
        return user.getUserDto();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto findUserById(Integer id) {
        return  userRepository.findById(id)
                .filter(Objects::nonNull)
                .map(User::getUserDto)
                .orElse(null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean existsUserWithKey(Integer key) {
        return userRepository.findByKey(key).isPresent();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoStudents() {
        userRepository.findAll()
                .stream()
                .filter(User::isDemoStudent)
                .forEach(user -> {
                    Integer userId = user.getId();
                    user.remove();
                    this.userRepository.delete(user);

                    DeleteAuthUserEvent deleteAuthUserEvent = new DeleteAuthUserEvent(userId);
                    domainEventPublisher.publish(USER_AGGREGATE_TYPE, String.valueOf(user.getId()),
                            Collections.singletonList(deleteAuthUserEvent));
                });
    }

    public void activateUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        user.setActive(true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserCourseExecutionsDto getUserCourseExecutions(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return user.getUserCourseExecutionsDto();
    }


    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new TutorException(USER_NOT_FOUND, id));
        user.remove();
        userRepository.delete(user);
    }

    public void removeCourseExecutions(Integer userId, List<CourseExecutionDto> courseExecutionDtoList) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        checkExecutionsExist(courseExecutionDtoList);
        removeCourseExecutionsFromUser(courseExecutionDtoList, user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeCourseExecutionsFromUser(List<CourseExecutionDto> courseExecutionDtoList, User user) {
        for (CourseExecutionDto dto: courseExecutionDtoList) {
            CourseExecution courseExecution = courseExecutionRepository.findById(dto.getCourseExecutionId()).get();
            user.removeCourse(courseExecution);
        }
    }

    private void checkExecutionsExist(List<CourseExecutionDto> courseExecutionDtoList) {
        for (CourseExecutionDto dto: courseExecutionDtoList) {
            courseExecutionRepository.findById(dto.getCourseExecutionId()).
                    orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, dto.getCourseExecutionId()));
        }
    }
}