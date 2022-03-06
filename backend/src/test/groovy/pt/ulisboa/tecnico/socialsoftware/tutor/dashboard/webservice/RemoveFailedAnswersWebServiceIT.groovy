package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice


import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveFailedAnswersWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def option
    def option2
    def quiz
    def question


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


        question = new QuestionDto()
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

        quiz = quizService.createQuiz(courseExecution.getCourseExecutionId(), quizDto)
    }

    def "demo student gets failed answers from dashboard then removes it"() {

        given: 'a student login'
        demoStudentLogin()
        def student = authUserService.demoStudentAuth(false).getUser()

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
        statementAnswerDto.setQuestionAnswerId(question.getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        answerService.concludeQuiz(statementQuizDto)

        and: 'a dashboard with updated answers'
        def dashboardDto = dashboardService.createDashboard(courseExecution.getCourseExecutionId(), student.getId())
        failedAnswerService.updateFailedAnswers(dashboardDto.getId())

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() +'/failedanswer/remove/' + statementAnswerDto.getQuestionAnswerId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response != null
        response.status == 200

        and: 'it is in the database'
        failedAnswerRepository.findAll().size() == 1

        and: 'it is not in the dashboard'
        def dashboard = dashboardRepository.findAll().get(0)
        dashboard.getAllFailedAnswers().isEmpty()

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
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
        quizRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
    }


}