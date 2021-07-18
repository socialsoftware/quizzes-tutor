package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo

<<<<<<< HEAD:backend/apigateway/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/apigateway/webservice/questionsubmission/UpdateQuestionSubmissionWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType

=======
>>>>>>> microservices:backend/tutor/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/tutor/questionsubmission/webservice/UpdateQuestionSubmissionWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.OptionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateQuestionSubmissionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def questionDto
    def questionSubmission
    def response
    def authStudent

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, CourseType.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT)
        student.setActive(true)
        student.addCourse(courseExecution)
        userRepository.save(student)
        authStudent = new AuthExternalUser(new UserSecurityInfo(student.getId(), USER_1_NAME, Role.STUDENT, false),
                USER_1_EMAIL, USER_1_EMAIL)
        authStudent.setActive(true)
        authStudent.addCourseExecution(courseExecution.getId())
        authStudent.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        courseExecution.addUser(student)
        authUserRepository.save(authStudent)

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

        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecution.getId())
        questionSubmissionDto.setSubmitterId(student.getId())
        questionSubmissionDto.setQuestion(questionDto)

        questionSubmissionService.createQuestionSubmission(questionSubmissionDto)
        questionSubmission = questionSubmissionRepository.findAll().get(0)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "edit question submission"() {
        given: "a questionSubmissionDto"
        def newQuestionDto = questionDto
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)

        and: "a questionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecution.getId())
        questionSubmissionDto.setSubmitterId(student.getId())
        questionSubmissionDto.setQuestion(newQuestionDto)

        when:
        response = restClient.put(
                path: '/submissions/' + questionSubmission.getId(),
                body: JsonOutput.toJson(questionSubmissionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the updated question"
        def questionSubmission = response.data
        questionSubmission.id != null
        questionSubmission.submitterId == student.getId()
        questionSubmission.status == QuestionSubmission.Status.IN_REVISION.name()
        questionSubmission.question != null
        questionSubmission.question.title == questionDto.getTitle()
        questionSubmission.question.content == questionDto.getContent()
        questionSubmission.question.status == Question.Status.SUBMITTED.name()
        questionSubmission.courseExecutionId == courseExecution.getId()
    }

    def cleanup() {
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        authUserRepository.deleteById(authStudent.getId())
        courseRepository.deleteById(course.getId())
    }
}


