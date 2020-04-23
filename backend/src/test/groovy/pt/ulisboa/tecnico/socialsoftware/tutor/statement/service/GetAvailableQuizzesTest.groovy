package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@DataJpaTest
class GetAvailableQuizzesTest extends Specification {
    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUIZ_TITLE = "Quiz title"
    public static final LocalDateTime BEFORE = DateHandler.now().minusDays(2)
    public static final LocalDateTime YESTERDAY = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOMORROW = DateHandler.now().plusDays(1)
    public static final LocalDateTime LATER = DateHandler.now().plusDays(2)

    @Autowired
    StatementService statementService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def user
    def courseDto
    def quiz
    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseDto = new CourseDto(courseExecution)

        user = new User('name', USERNAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        userRepository.save(user)
    }

    @Unroll
    def "returns available quiz with: quizType=#quizType | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quiz.setCourseExecution(courseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'the return statement contains one quiz'
        statementQuizDtos.size() == 1
        def statementQuizDto = statementQuizDtos.get(0)
        statementQuizDto.getId() != null
        statementQuizDto.getQuizAnswerId() != null
        statementQuizDto.getTitle() == QUIZ_TITLE
        statementQuizDto.isOneWay()
        statementQuizDto.getAvailableDate() == DateHandler.toISOString(BEFORE)
        statementQuizDto.getConclusionDate() == DateHandler.toISOString(conclusionDate)
        statementQuizDto.getQuestions().size() == 0
        statementQuizDto.getAnswers().size() == 0
        statementQuizDto.getTimeToAvailability() == null

        if (quiz.getConclusionDate()) {
            statementQuizDto.getTimeToSubmission() == ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getConclusionDate())
        } else {
            statementQuizDto.getTimeToSubmission() == null
        }

        if (quiz.getResultsDate()) {
            statementQuizDto.getTimeToResults() == ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getResultsDate())
        } else {
            statementQuizDto.getTimeToResults() == null
        }

        where:
        quizType                | conclusionDate | resultsDate
        Quiz.QuizType.PROPOSED  | null           | null
        Quiz.QuizType.PROPOSED  | null           | LATER
        Quiz.QuizType.PROPOSED  | TOMORROW       | null
        Quiz.QuizType.PROPOSED  | TOMORROW       | LATER
        Quiz.QuizType.IN_CLASS  | TOMORROW       | null
        Quiz.QuizType.IN_CLASS  | TOMORROW       | LATER
    }

    def 'returns a qrOnly quiz if it was scanned already (if it has a quizAnswer)'() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setQrCodeOnly(true)
        quiz.setOneWay(true)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(TOMORROW)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'the return statement contains one quiz'
        statementQuizDtos.size() == 1
        def statementQuizDto = statementQuizDtos.get(0)
        statementQuizDto.getId() != null
        statementQuizDto.getQuizAnswerId() != null
        statementQuizDto.getTitle() == QUIZ_TITLE
        statementQuizDto.isOneWay()
        statementQuizDto.getAvailableDate() == DateHandler.toISOString(BEFORE)
        statementQuizDto.getConclusionDate() == DateHandler.toISOString(TOMORROW)
        statementQuizDto.getTimeToAvailability() == null
        statementQuizDto.getTimeToSubmission() == null
        statementQuizDto.getTimeToResults() == null
        statementQuizDto.getQuestions().size() == 0
        statementQuizDto.getAnswers().size() == 0
    }

    @Unroll
    def "does not return quiz with: quizType=#quizType | qrOnly=#qrOnly | availableDate=#availableDate | conclusionDate=#conclusionDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setCourseExecution(courseExecution)
        quiz.setQrCodeOnly(qrOnly)
        quizRepository.save(quiz)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'no quiz is returned'
        statementQuizDtos.size() == 0

        where:
        quizType                | qrOnly | availableDate  | conclusionDate
        Quiz.QuizType.PROPOSED  | true   | YESTERDAY      | TOMORROW
        Quiz.QuizType.IN_CLASS  | true   | YESTERDAY      | TOMORROW
        Quiz.QuizType.PROPOSED  | true   | YESTERDAY      | null
        Quiz.QuizType.PROPOSED  | false  | TOMORROW       | LATER
        Quiz.QuizType.IN_CLASS  | false  | TOMORROW       | LATER
        Quiz.QuizType.PROPOSED  | false  | BEFORE         | YESTERDAY
        Quiz.QuizType.IN_CLASS  | false  | BEFORE         | YESTERDAY
    }

    def 'does not return a completed quiz'() {
        given: 'a completed quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(TOMORROW)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'no quiz is returned'
        statementQuizDtos.size() == 0
     }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {
        @Bean
        StatementService statementService() {
            return new StatementService()
        }
        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
        @Bean
        QuizService quizService() {
            return new QuizService()
        }
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
