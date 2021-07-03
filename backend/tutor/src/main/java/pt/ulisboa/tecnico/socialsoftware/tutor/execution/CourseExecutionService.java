package pt.ulisboa.tecnico.socialsoftware.tutor.execution;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.events.AddCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.DeleteAuthUserEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.RemoveCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class CourseExecutionService {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private EventBus eventBus;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto getCourseExecutionById(int courseExecutionId) {
        return courseExecutionRepository.findById(courseExecutionId)
                .map(CourseExecution::getDto)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseExecutionDto> getCourseExecutions(Role role) {
        return courseExecutionRepository.findAll().stream()
                .filter(courseExecution -> role.equals(Role.ADMIN) ||
                        (role.equals(Role.DEMO_ADMIN) && courseExecution.getCourse().getName().equals(TutorDemoUtils.COURSE_NAME)))
                .map(CourseExecution::getDto)
                .sorted(Comparator
                        .comparing(CourseExecutionDto::getName)
                        .thenComparing(CourseExecutionDto::getAcademicTerm))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto createTecnicoCourseExecution(CourseExecutionDto courseExecutionDto) {
        courseExecutionDto.setCourseExecutionType(CourseType.TECNICO);
        courseExecutionDto.setCourseType(CourseType.TECNICO);

        Course course = getCourse(courseExecutionDto.getName(), CourseType.TECNICO);

        CourseExecution courseExecution = course.getCourseExecution(courseExecutionDto.getAcronym(), courseExecutionDto.getAcademicTerm(), courseExecutionDto.getCourseExecutionType())
                .orElseGet(() -> createCourseExecution(course, courseExecutionDto));
        courseExecution.setStatus(CourseExecutionStatus.ACTIVE);
        return courseExecution.getDto();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addUserToTecnicoCourseExecution(Integer userId, int courseExecutionId) {
        CourseExecution courseExecution = this.courseExecutionRepository.findById(courseExecutionId).orElse(null);
        User user = this.userRepository.findById(userId).orElse(null);

        if (user != null && courseExecution != null) {
            courseExecution.addUser(user);
            user.addCourse(courseExecution);
            //Auth subscribes to this event and adds course execution to authUser
            AddCourseExecutionEvent addCourseExecutionEvent = new AddCourseExecutionEvent(userId, courseExecutionId);
            eventBus.post(addCourseExecutionEvent);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto createExternalCourseExecution(CourseExecutionDto courseExecutionDto) {
        courseExecutionDto.setCourseExecutionType(CourseType.EXTERNAL);

        Course course = getCourse(courseExecutionDto.getName(), courseExecutionDto.getCourseType());

        CourseExecution courseExecution = createCourseExecution(course, courseExecutionDto);

        courseExecution.setStatus(CourseExecutionStatus.ACTIVE);
        return courseExecution.getDto();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeCourseExecution(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        courseExecution.remove();
        courseExecutionRepository.delete(courseExecution);

        RemoveCourseExecutionEvent removeCourseExecutionEvent = new RemoveCourseExecutionEvent(courseExecutionId);
        eventBus.post(removeCourseExecutionEvent);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void anonymizeCourseExecutionUsers(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        for (User user : courseExecution.getUsers()) {
            String oldUsername = user.getUsername();
            DeleteAuthUserEvent deleteAuthUser = new DeleteAuthUserEvent(user.getId());
            eventBus.post(deleteAuthUser);
            String newUsername = user.getUsername();
            questionAnswerItemRepository.updateQuestionAnswerItemUsername(oldUsername, newUsername);
            String role = user.getRole().toString();
            String roleCapitalized = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
            user.setName(String.format("%s %s", roleCapitalized, user.getId()));
            user.setUsername(null);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> getStudents(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElse(null);
        if (courseExecution == null) {
            return new ArrayList<>();
        }
        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole().equals(Role.STUDENT))
                .sorted(Comparator.comparing(User::getName))
                .map(User::getStudentDto)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> getTeachers(Integer courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElse(null);
        if (courseExecution == null) {
            return new ArrayList<>();
        }
        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole().equals(Role.TEACHER))
                .map(User::getUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> importQuestions(InputStream inputStream, Integer executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

        QuestionsXmlImport questionsXmlImport = new QuestionsXmlImport();

        return questionsXmlImport.importQuestions(inputStream, this.questionService, this.courseRepository, courseExecution);
    }

    private Course getCourse(String name, CourseType type) {
      if (type == null)
            throw new TutorException(INVALID_TYPE_FOR_COURSE);

        return courseRepository.findByNameType(name, type.toString())
                .orElseGet(() -> courseRepository.save(new Course(name, type)));
    }

    private CourseExecution createCourseExecution(Course existingCourse, CourseExecutionDto courseExecutionDto) {
        CourseExecution courseExecution = new CourseExecution(existingCourse, courseExecutionDto.getAcronym(), courseExecutionDto.getAcademicTerm(), courseExecutionDto.getCourseExecutionType(), DateHandler.toLocalDateTime(courseExecutionDto.getEndDate()));
        courseExecutionRepository.save(courseExecution);
        return courseExecution;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto getDemoCourse() {
        CourseExecution courseExecution =  this.courseExecutionRepository.findByFields(TutorDemoUtils.COURSE_ACRONYM, TutorDemoUtils.COURSE_ACADEMIC_TERM, CourseType.TECNICO.toString()).orElse(null);

        if (courseExecution == null) {
            return createTecnicoCourseExecution(new CourseExecutionDto(TutorDemoUtils.COURSE_NAME, TutorDemoUtils.COURSE_ACRONYM, TutorDemoUtils.COURSE_ACADEMIC_TERM));
        }
        return courseExecution.getDto();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto getCourseExecutionByFields(String acronym, String academicTerm, String type) {
        CourseExecution courseExecution =  this.courseExecutionRepository.findByFields(acronym, academicTerm, type).orElse(null);
        CourseExecutionDto courseExecutionDto = null;
        if (courseExecution != null) {
            courseExecutionDto = courseExecution.getDto();
        }
        return courseExecutionDto;
    }

    public CourseExecution getDemoCourseExecution() {
        return this.courseExecutionRepository.findByFields(TutorDemoUtils.COURSE_ACRONYM, TutorDemoUtils.COURSE_ACADEMIC_TERM, CourseType.TECNICO.toString()).orElseGet(() -> {
            Course course = getCourse(TutorDemoUtils.COURSE_NAME, CourseType.TECNICO);
            CourseExecution courseExecution = new CourseExecution(course, TutorDemoUtils.COURSE_ACRONYM, TutorDemoUtils.COURSE_ACADEMIC_TERM, CourseType.TECNICO, DateHandler.now().plusDays(1));
            return courseExecutionRepository.save(courseExecution);
        });
    }

    private CourseExecution getExternalCourseExecution(Integer courseExecutionId) {
        CourseExecution execution = courseExecutionRepository
                .findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
        checkExternalExecution(execution);
        return execution;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CourseExecutionDto deleteExternalInactiveUsers(Integer courseExecutionId, List<Integer> usersId){
        CourseExecution courseExecution = getExternalCourseExecution(courseExecutionId);
        deleteUsersOfUserIds(usersId, courseExecution);
        return courseExecution.getDto();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicDto> findAvailableTopicsByCourseExecution(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        return courseExecution.findAvailableTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(Topic::getDto).collect(Collectors.toList());
    }

    private void deleteUsersOfUserIds(List<Integer> usersId, CourseExecution courseExecution) {
        usersId = getExecutionFilteredIds(usersId, courseExecution);
        deleteUsers(usersId);
    }

    private void deleteUsers(List<Integer> usersId) {
        for (Integer id : usersId) {
            User user = userRepository
                    .findById(id)
                    .orElseThrow(() -> new TutorException((USER_NOT_FOUND)));
            user.remove();
            userRepository.delete(user);

            DeleteAuthUserEvent deleteAuthUser = new DeleteAuthUserEvent(id);
            eventBus.post(deleteAuthUser);
        }
    }

    private List<Integer> getExecutionFilteredIds(List<Integer> usersId, CourseExecution courseExecution) {
        List<Integer> executionUserIdList = courseExecution.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        return usersId.stream()
                .filter(executionUserIdList::contains)
                .collect(Collectors.toList());
    }

    private void checkExternalExecution(CourseExecution courseExecution) {
        if (!courseExecution.getType().equals(CourseType.EXTERNAL)) {
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL);
        }
    }

    @Override
    public String toString() {
        return "CourseExecutionService{" +
                "questionService=" + questionService +
                ", courseRepository=" + courseRepository +
                ", courseExecutionRepository=" + courseExecutionRepository +
                ", userRepository=" + userRepository +
                ", questionAnswerItemRepository=" + questionAnswerItemRepository +
                ", eventBus=" + eventBus +
                '}';
    }
}