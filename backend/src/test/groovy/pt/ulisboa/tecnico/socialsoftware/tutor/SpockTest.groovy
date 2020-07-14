package pt.ulisboa.tecnico.socialsoftware.tutor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuizAnswerItemRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.SubmissionService
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository.ReviewRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class SpockTest extends Specification {
    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"
    public static final String USER_1_USERNAME = "User 1 Username"
    public static final String USER_2_USERNAME = "User 2 Username"
    public static final String USER_3_USERNAME = "User 3 Username"

    public static final String ASSESSMENT_1_TITLE = "Assessment 1 Title"
    public static final String ASSESSMENT_2_TITLE = "Assessment 2 Title"

    public static final String COURSE_1_NAME = "Course 1 Name"
    public static final String COURSE_1_ACADEMIC_TERM = "1º Semestre 2019/2020"
    public static final String COURSE_2_ACADEMIC_TERM = "2º Semestre 2019/2020"
    public static final String COURSE_1_ACRONYM = "C12"
    public static final String COURSE_2_ACRONYM = "C22"

    public static final String TOPIC_1_NAME = "Topic 1 Name"
    public static final String TOPIC_2_NAME = "Topic 2 Name"
    public static final String TOPIC_3_NAME = "Topic 3 Name"

    public static final String IMAGE_1_URL = "Image 1 URL"

    public static final LocalDateTime LOCAL_DATE_BEFORE = DateHandler.now().minusDays(2)
    public static final LocalDateTime LOCAL_DATE_YESTERDAY = DateHandler.now().minusDays(1)
    public static final LocalDateTime LOCAL_DATE_TODAY = DateHandler.now()
    public static final LocalDateTime LOCAL_DATE_TOMORROW = DateHandler.now().plusDays(1)
    public static final LocalDateTime LOCAL_DATE_LATER = DateHandler.now().plusDays(2)

    public static final String STRING_DATE_LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))
    public static final String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())
    public static final String STRING_DATE_TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))

    public static final String QUESTION_1_CONTENT = "Question 1 Content\n ![image][image]\n question content"
    public static final String QUESTION_2_CONTENT = "Question 2 Content\n ![image][image]\n question content"
    public static final String QUESTION_3_CONTENT = "Question 3 Content\n ![image][image]\n question content"
    public static final String QUESTION_4_CONTENT = "Question 4 Content\n ![image][image]\n question content"
    public static final String QUESTION_5_CONTENT = "Question 5 Content\n ![image][image]\n question content"
    public static final String QUESTION_1_TITLE = "Question 1 Title"
    public static final String QUESTION_2_TITLE = "Question 2 Title"
    public static final String QUESTION_3_TITLE = "Question 3 Title"
    public static final String QUESTION_4_TITLE = "Question 4 Title"
    public static final String QUESTION_5_TITLE = "Question 5 Title"

    public static final String OPTION_1_CONTENT = "Option 1 Content"
    public static final String OPTION_2_CONTENT = "Option 2 Content"

    public static final String QUIZ_TITLE = "Quiz title"

    public static final String REVIEW_1_JUSTIFICATION = "Review Justification 1"
    public static final String REVIEW_2_JUSTIFICATION = "Review Justification 2"
    public static final String REVIEW_3_JUSTIFICATION = "Review Justification 3"
    public static final String SUBMISSION_ARGUMENT = "Submission Argument"

    @Autowired
    AuthService authService

    @Autowired
    AnswerService answerService

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    AssessmentService assessmentService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseService courseService

    @Autowired
    ImageRepository imageRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionService questionService

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizAnswerItemRepository quizAnswerItemRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizService quizService

    @Autowired
    StatementService statementService

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicService topicService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    SubmissionService submissionService

    @Autowired
    SubmissionRepository submissionRepository

    @Autowired
    ReviewRepository reviewRepository

    Course course
    @Shared
    CourseExecution courseExecution

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
    }
}
