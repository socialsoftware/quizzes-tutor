package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.webservice

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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetCourseExecutionQuestionSubmissionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student1
    def student2
    def questionDto
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student1 = new User(USER_3_NAME, USER_3_EMAIL, USER_3_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student1.addCourse(courseExecution)
        courseExecution.addUser(student1)
        userRepository.save(student1)
        student2 = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student2.addCourse(courseExecution)
        courseExecution.addUser(student2)
        userRepository.save(student2)

        questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.getQuestionDetailsDto().setOptions(options)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "get course execution question submissions"() {
        given: "questionSubmissions"

        def questionSubmission1Dto = new QuestionSubmissionDto()
        questionSubmission1Dto.setCourseExecutionId(courseExecution.getId())
        questionSubmission1Dto.setSubmitterId(student1.getId())
        questionSubmission1Dto.setQuestion(questionDto)
        questionSubmissionService.createQuestionSubmission(questionSubmission1Dto)

        def questionSubmission2Dto = new QuestionSubmissionDto()
        questionSubmission2Dto.setCourseExecutionId(courseExecution.getId())
        questionSubmission2Dto.setSubmitterId(student2.getId())
        questionSubmission2Dto.setQuestion(questionDto)
        questionSubmissionService.createQuestionSubmission(questionSubmission2Dto)

        def questionSubmission3Dto = new QuestionSubmissionDto()
        questionSubmission3Dto.setCourseExecutionId(courseExecution.getId())
        questionSubmission3Dto.setSubmitterId(student2.getId())
        questionSubmission3Dto.setQuestion(questionDto)
        questionSubmissionService.createQuestionSubmission(questionSubmission3Dto)

        when:
        response = restClient.get(
                path: '/submissions/'+courseExecution.getId()+'/execution',
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct question submissions"
        def submissions = response.data
        submissions.get(0).id != null
        submissions.get(1).id != null
        submissions.get(2).id != null
        submissions.get(0).submitterId == student1.getId()
        submissions.get(1).submitterId == student2.getId()
        submissions.get(2).submitterId == student2.getId()
        submissions.get(0).courseExecutionId == courseExecution.getId()
        submissions.get(1).courseExecutionId == courseExecution.getId()
        submissions.get(2).courseExecutionId == courseExecution.getId()
    }

    def cleanup() {
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student1.getId())
        userRepository.deleteById(student2.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())

        courseRepository.deleteById(course.getId())
    }
}







