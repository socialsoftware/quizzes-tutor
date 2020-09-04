package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetQuestionSubmissionReviewsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student
    def questionSubmission
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, true, false)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.IN_REVISION.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecution.getId())
        questionSubmissionDto.setSubmitterId(student.getId())
        questionSubmissionDto.setQuestion(questionDto)

        questionSubmissionService.createQuestionSubmission(questionSubmissionDto)
        questionSubmission = questionSubmissionRepository.findAll().get(0)

        teacher = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL, User.Role.TEACHER, true, false)
        teacher.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
    }

    def "get question submission review"() {
        given: "a review"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus(Review.Status.AVAILABLE.name())
        questionSubmissionService.createReview(reviewDto)

        when:
        response = restClient.get(
                path: '/submissions/'+questionSubmission.getId()+'/reviews',
                query: ['executionId': courseExecution.getId()],
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct question submissions"
        def reviews = response.data
        reviews.get(0).id != null
        reviews.get(0).userId == teacher.getId()
        reviews.get(0).questionSubmissionId == questionSubmission.getId()
        reviews.get(0).comment == REVIEW_1_COMMENT
        reviews.get(0).status == Review.Status.AVAILABLE.name()
        reviews.get(0).name == teacher.getName()
        reviews.get(0).username == teacher.getUsername()
    }

    def cleanup() {
        persistentCourseCleanup()

        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())

        courseRepository.deleteById(course.getId())
    }
}







