package pt.ulisboa.tecnico.socialsoftware.tournament

import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.Languages
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@ActiveProfiles("test")
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

    public static final int NUMBER_OF_QUESTIONS = 1

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    Course externalCourse
    @Shared
    CourseExecution externalCourseExecution

    def createExternalCourseAndExecution() {
        externalCourse = new Course(COURSE_1_NAME, CourseType.TECNICO)
        courseRepository.save(externalCourse)

        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecution)
    }

    def persistentCourseCleanup() {
        Course c
        CourseExecution ce
        if(courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.TECNICO as String).isPresent()){
            ce = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.TECNICO as String).get()
            c = ce.getCourse()
            courseExecutionRepository.dissociateCourseExecutionUsers(ce.getId())
            courseExecutionRepository.deleteById(ce.getId())
            courseRepository.deleteById(c.getId())
        }
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
                path: '/auth/demo/student'
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
