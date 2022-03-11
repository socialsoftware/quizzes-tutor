package pt.ulisboa.tecnico.socialsoftware.tutor

import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DifficultQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.WeeklyScoreService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.SameDifficulty
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.SamePercentage
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.SameDifficultyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.SamePercentageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.SameQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.WeeklyScoreRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.FailedAnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.FailedAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ReplyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Languages
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.QuestionSubmissionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.ReviewRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

class SpockTest extends Specification {

    @Value('${spring.mail.username}')
    public String mailerUsername

    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"
    public static final String DEMO_STUDENT_NAME = "Demo Student"
    public static final String DEMO_TEACHER_NAME = "Demo Teacher"
    public static final String DEMO_ADMIN_NAME = "Demo Admin"

    public static final String USER_1_USERNAME = "a@a.a"
    public static final String USER_2_USERNAME = "a@a.b"
    public static final String USER_3_USERNAME = "user3username"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_2_EMAIL = "user2@mail.com"
    public static final String USER_3_EMAIL = "user3@mail.com"
    public final static String USER_1_PASSWORD = "1234"
    public final static String USER_2_PASSWORD = "4321"
    public static final String USER_1_TOKEN = "1a2b3c"
    public static final String USER_2_TOKEN = "c3b2a1"

    public static final String ASSESSMENT_1_TITLE = "Assessment 1 Title"
    public static final String ASSESSMENT_2_TITLE = "Assessment 2 Title"

    public static final String COURSE_1_NAME = "Course 1 Name"
    public static final String COURSE_2_NAME = "Course 2 Name"
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

    public static final String STRING_DATE_BEFORE = DateHandler.toISOString(DateHandler.now().minusDays(2))
    public static final String STRING_DATE_YESTERDAY = DateHandler.toISOString(DateHandler.now().minusDays(1))
    public static final String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())
    public static final String STRING_DATE_TODAY_PLUS_10_MINUTES = DateHandler.toISOString(DateHandler.now().plusMinutes(10))
    public static final String STRING_DATE_TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    public static final String STRING_DATE_TOMORROW_PLUS_10_MINUTES = DateHandler.toISOString(DateHandler.now().plusDays(1).plusMinutes(10))
    public static final String STRING_DATE_LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))
    public static final String STRING_DATE_LATER_PLUS_10_MINUTES = DateHandler.toISOString(DateHandler.now().plusDays(2).plusMinutes(10))

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

    public static final String CODE_QUESTION_1_CODE = "class Simple { {{drop1}} }"
    public static final Languages CODE_QUESTION_1_LANGUAGE = Languages.Java


    public static final String OPTION_1_CONTENT = "Option 1 Content"
    public static final String OPTION_2_CONTENT = "Option 2 Content"

    public static final String ROLE_STUDENT = "ROLE_STUDENT"
    public static final String ROLE_TEACHER = "ROLE_TEACHER"
    public static final String ROLE_ADMIN = "ROLE_ADMIN"
    public static final String ROLE_DEMO_ADMIN = "ROLE_DEMO_ADMIN"

    public static final String QUIZ_TITLE = "Quiz title"
    public static final String CSVFILE = System.getProperty("user.dir") + "/src/test/resources/importUsers.csv"
    public static final String CSVBADFORMATFILE = System.getProperty("user.dir") + "/src/test/resources/csvBadFormatFile.csv"
    public static final String CSVIMPORTUSERSBADROLEFORMAT = System.getProperty("user.dir") + "/src/test/resources/csvImportUsersBadRoleFormat.csv"
    public static final int NUMBER_OF_USERS_IN_FILE = 5

    public static final int NUMBER_OF_QUESTIONS = 1

    public static final String REVIEW_1_COMMENT = "Review Comment 1"

    public static final String DISCUSSION_MESSAGE = "Discussion Message"
    public static final String DISCUSSION_REPLY = "Discussion Reply"

    @Autowired
    AuthUserService authUserService

    @Autowired
    AnswerService answerService

    @Autowired
    AnswerDetailsRepository answerDetailsRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    AssessmentService assessmentService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionService courseService

    @Autowired
    DashboardService dashboardService

    @Autowired
    DashboardRepository dashboardRepository

    @Autowired
    WeeklyScoreService weeklyScoreService

    @Autowired
    WeeklyScoreRepository weeklyScoreRepository

    @Autowired
    SamePercentageRepository samePercentageRepository

    @Autowired
    SameQuestionRepository sameQuestionRepository

    @Autowired
    SameDifficultyRepository sameDifficultyRepository

    @Autowired
    DifficultQuestionService difficultQuestionService

    @Autowired
    DifficultQuestionRepository difficultQuestionRepository

    @Autowired
    FailedAnswerService failedAnswerService

    @Autowired
    FailedAnswerRepository failedAnswerRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuestionAnswerItemRepository questionAnswerItemRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionDetailsRepository questionDetailsRepository

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
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicService topicService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepository authUserRepository

    @Autowired
    QuestionSubmissionService questionSubmissionService

    @Autowired
    QuestionSubmissionRepository questionSubmissionRepository

    @Autowired
    ReviewRepository reviewRepository

    @Autowired
    UserApplicationalService userServiceApplicational

    @Autowired
    Mailer mailer

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    DiscussionRepository discussionRepository

    @Autowired
    DiscussionService discussionService

    @Autowired
    ReplyRepository replyRepository

    @Autowired
    DemoUtils demoUtils

    Course externalCourse
    @Shared
    CourseExecution externalCourseExecution

    def createExternalCourseAndExecution() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecution)
    }

    RESTClient restClient

    def demoAdminLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/admin'
        )
        restClient.headers['Authorization']  = "Bearer " + loginResponse.data.token
    }

    def demoStudentLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/student',
                query: ['createNew': false]
        )
        restClient.headers['Authorization']  = "Bearer " + loginResponse.data.token
    }

    def demoTeacherLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/teacher'
        )
        restClient.headers['Authorization']  = "Bearer " + loginResponse.data.token
    }

    def createdUserLogin(email, password) {
        def loggedUser = restClient.get(
                path: '/auth/external',
                query: [
                        email: email,
                        password: password,
                ],
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization']  = "Bearer " + loggedUser.data.token
    }
}
