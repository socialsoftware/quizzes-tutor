package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthTecnicoUser

<<<<<<< HEAD:backend/apigateway/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/apigateway/webservice/questionsubmission/ToggleStudentNotificationReadWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo

=======
>>>>>>> microservices:backend/tutor/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/tutor/questionsubmission/webservice/ToggleStudentNotificationReadWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.OptionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ToggleStudentNotificationReadWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def authStudent
    def teacher
    def authTeacher
    def questionDto
    def questionSubmission
    def response

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
        authStudent = new AuthTecnicoUser(new UserSecurityInfo(student.getId(), USER_1_NAME, Role.STUDENT, false),
                USER_1_EMAIL, USER_1_EMAIL)
        authStudent.addCourseExecution(courseExecution.getId())
        authStudent.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        courseExecution.addUser(student)
        authUserRepository.save(authStudent)

        teacher = new User(USER_2_NAME, USER_2_EMAIL, Role.TEACHER)
        teacher.setActive(true)
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)
        authTeacher = new AuthTecnicoUser(new UserSecurityInfo(teacher.getId(), USER_2_NAME, Role.TEACHER, false),
                USER_2_EMAIL, USER_2_EMAIL)
        authTeacher.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        authTeacher.addCourseExecution(courseExecution.getId())
        courseExecution.addUser(teacher)
        authUserRepository.save(authTeacher)

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

        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
    }

    def "notify student on question submission"() {
        when:
        response = restClient.put(
                path: '/submissions/'+questionSubmission.getId()+'/toggle-notification-student',
                query: ['hasRead': true],
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
    }

    def cleanup() {
        userRepository.deleteById(student.getId())
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        authUserRepository.deleteById(authStudent.getId())
        authUserRepository.deleteById(authTeacher.getId())
        courseRepository.deleteById(course.getId())
    }
}



