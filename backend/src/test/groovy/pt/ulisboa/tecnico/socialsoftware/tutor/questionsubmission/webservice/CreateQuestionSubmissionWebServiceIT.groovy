package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionSubmissionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def questionSubmissionDto
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "create question submission for course execution"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        and: "a questionSubmissionDto"
        questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecution.getId())
        questionSubmissionDto.setSubmitterId(student.getId())
        questionSubmissionDto.setQuestion(questionDto)

        when:
        response = restClient.post(
                path: '/submissions/' + courseExecution.getId(),
                body: JsonOutput.toJson(questionSubmissionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct questionSubmission"
        def questionSubmission = response.data
        questionSubmission.id != null
        questionSubmission.submitterId == student.getId()
        questionSubmission.status == QuestionSubmission.Status.IN_REVISION.name()
        questionSubmission.question != null
        questionSubmission.question.title == questionDto.getTitle()
        questionSubmission.question.content == questionDto.getContent()
        questionSubmission.question.status == Question.Status.SUBMITTED.name()
    }

    def cleanup() {
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())

        courseRepository.deleteById(course.getId())
    }
}

