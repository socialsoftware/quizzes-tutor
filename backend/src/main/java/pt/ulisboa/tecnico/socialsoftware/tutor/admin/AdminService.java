package pt.ulisboa.tecnico.socialsoftware.tutor.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.time.LocalDate;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHUSER_NOT_FOUND_BY_USERNAME;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;

@Service
public class AdminService {
    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionAnswerItemRepository questionAnswerItemRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void anonymizeCourseExecutionUsers(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

        int year = LocalDate.now().getYear();

        for (User user : courseExecution.getUsers()) {
            if (user.isStudent() && user.getAuthUser() != null && user.getCourseExecutions().stream()
                    .filter(courseExecution1 -> courseExecution1.getType().equals(Course.Type.TECNICO))
                    .noneMatch(courseExecution1 -> courseExecution != courseExecution1 && courseExecution1.getYear() >= year - 1)) {
                anonymizeUser(user);
            }
        }

        courseExecution.setStatus(CourseExecution.Status.HISTORIC);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void anonymizeUser(String username) {
        User user = authUserRepository.findAuthUserByUsername(username)
                .map(AuthUser::getUser)
                .orElseThrow(() -> new TutorException(AUTHUSER_NOT_FOUND_BY_USERNAME, username));

        anonymizeUser(user);
    }

    private void anonymizeUser(User user) {
        String oldUsername = user.getUsername();
        AuthUser authUser = user.getAuthUser();
        authUser.remove();
        authUserRepository.delete(authUser);
        String newUsername = user.getUsername();
        questionAnswerItemRepository.updateQuestionAnswerItemUsername(oldUsername, newUsername);
        String role = user.getRole().toString();
        String roleCapitalized = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
        user.setName(String.format("%s %s", roleCapitalized, user.getId()));
    }
}
