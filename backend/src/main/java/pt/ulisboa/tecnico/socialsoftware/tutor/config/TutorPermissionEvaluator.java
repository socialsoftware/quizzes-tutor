package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        int userId = ((User) authentication.getPrincipal()).getId();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(userId).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseDto courseDto = courseService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userService.userHasAnExecutionOfCourse(userId, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(userId, id);
                case "QUESTION.ACCESS":
                    Question question = questionRepository.findQuestionWithCourseById(id).orElse(null);
                    if (question != null) {
                        return userService.userHasAnExecutionOfCourse(userId, question.getCourse().getId());
                    }
                    return false;
                case "TOPIC.ACCESS":
                    Topic topic = topicRepository.findTopicWithCourseById(id).orElse(null);
                    if (topic != null) {
                        return userService.userHasAnExecutionOfCourse(userId, topic.getCourse().getId());
                    }
                    return false;
                case "ASSESSMENT.ACCESS":
                    Integer courseExecutionId = assessmentRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(userId, courseExecutionId);
                    }
                    return false;
                case "QUIZ.ACCESS":
                    courseExecutionId = quizRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(userId, courseExecutionId);
                    }
                    return false;
                case "TOURNAMENT.ACCESS":
                    courseExecutionId = tournamentRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(userId, courseExecutionId);
                    }
                    return false;
                case "TOURNAMENT.OWNER":
                    Tournament tournament = tournamentRepository.findById(id).orElse(null);
                    User user = ((User) authentication.getPrincipal());
                    if (tournament != null) {
                        tournament.checkCreator(user);
                    }
                    courseExecutionId = tournamentRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(userId, courseExecutionId);
                    }
                    return false;
                default: return false;
            }
        }

        return false;
    }

    private boolean userHasThisExecution(int userId, int courseExecutionId) {
        return userRepository.countUserCourseExecutionsPairById(userId, courseExecutionId) == 1;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
