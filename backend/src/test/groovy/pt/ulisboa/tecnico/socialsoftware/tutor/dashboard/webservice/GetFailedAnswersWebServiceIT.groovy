package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetFailedAnswersWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def option
    def option2
    def quizAnswer
    def quiz


    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)

        and: 'a demo course execution'
        courseExecution = courseService.getDemoCourse()

        and: 'a quiz with future conclusionDate'
        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle("Quiz Title")
        quizDto.setType(Quiz.QuizType.PROPOSED.toString())
        quizDto.setAvailableDate(STRING_DATE_YESTERDAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)


        def question = new QuestionDto()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setStatus(Question.Status.AVAILABLE.name())
        
        def questionDetails = new MultipleChoiceQuestionDto()

        option = new OptionDto()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)

        option2 = new OptionDto()
        option2.setContent("Option Content")
        option2.setCorrect(false)
        option2.setSequence(1)

        def options = new ArrayList<OptionDto>()
        options.add(option)
        options.add(option2)

        questionDetails.setOptions(options)
        question.setQuestionDetailsDto(questionDetails)

        def questionDto = questionService.createQuestion(courseExecution.getCourseExecutionId(), question)

        def questions = new ArrayList<QuestionDto>()
        questions.add(questionDto)
        quizDto.setQuestions(questions)

        quiz = quizService.createQuiz(courseExecution.getCourseExecutionId(), quizDto)
    }

    def "demo student gets failed answers from dashboard"() {

        given: 'a student login'
        demoStudentLogin()
        def student = authUserService.demoStudentAuth(false).getUser()
        courseExecution.addUser(student)

        and: 'a failed answer to the quiz'
        def quizAnswer = answerService.createQuizAnswer(student.getId(), quiz.getId())

        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def multipleChoiceAnswerDto = new MultipleChoiceStatementAnswerDetailsDto()
        multipleChoiceAnswerDto.setOptionId(option.getId())
        multipleChoiceAnswerDto.setOptionId(option2.getId())
        statementAnswerDto.setAnswerDetails(multipleChoiceAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getQuestion().getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        answerService.concludeQuiz(statementQuizDto)

        and: 'a dashboard'
        def dashboardDto = dashboardService.createDashboard(courseExecution.getId(), student.getId())

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecution.getId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        and: "has value"
        response.data.id != null
        and: 'it is in the database'
        dashboardRepository.findAll().size() == 1
        failedAnswerRepository.findAll().size() == 1
        response.data.getFailedAnswers().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        failedAnswerRepository.deleteAll()
    }


    def cleanup(){
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        quizQuestionRepository.deleteAll()
        teacherAnswerRepository.deleteAll()
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
        quizRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
    }


}