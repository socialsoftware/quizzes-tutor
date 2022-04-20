package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateDifficultQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def student
    def dashboard
    def question

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        def now = DateHandler.now()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and:
        def optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        and:
        def optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and:
        def topic = new Topic()
        topic.setName(TOPIC_1_NAME)
        topic.setCourse(externalCourse)
        question.addTopic(topic)
        topicRepository.save(topic)
        and:
        def assessmentDto = new AssessmentDto()
        assessmentDto.title = ASSESSMENT_1_TITLE
        assessmentDto.status = 'AVAILABLE'
        assessmentDto.sequence = 1
        def topicDto = new TopicDto(topic)
        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(topicDto)
        assessmentDto.addTopicConjunction(topicConjunctionDto)
        assessmentService.createAssessment(externalCourseExecution.id, assessmentDto)
        and:
        def quiz = new Quiz()
        quiz.setType("PROPOSED")
        quiz.setAvailableDate(now.minusMinutes(5))
        quiz.setResultsDate(now)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)
        and:
        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)
    }

    def "student updates difficult questions"() {
        given:
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions',
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200
        and:
        response.data.size() == 1
        def resultDifficultQuestion = response.data.get(0)
        !resultDifficultQuestion.removed
        resultDifficultQuestion.removedDate == null
        resultDifficultQuestion.percentage == 0
        resultDifficultQuestion.questionDto.id == question.getId()
        and:
        difficultQuestionRepository.findAll().size() == 1
    }


    def "teacher cant update student's difficult questions"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student cant update another students difficult questionss"() {
        given:
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(newStudent)
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        difficultQuestionRepository.deleteAll()
        dashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}