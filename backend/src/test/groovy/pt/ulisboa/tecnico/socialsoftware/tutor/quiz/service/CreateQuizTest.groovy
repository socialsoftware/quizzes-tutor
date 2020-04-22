package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String QUESTION_TITLE = 'question title'
    public static final String VERSION = 'B'
    public static final LocalDateTime AVAILABLE_DATE = DateHandler.now()
    public static final LocalDateTime CONCLUSION_DATE = DateHandler.now().plusDays(1)
    public static final LocalDateTime RESULTS_DATE = DateHandler.now().plusDays(2)

    @Autowired
    QuizService quizService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    def course
    def courseExecution
    def quizDto
    def questionDto

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(DateHandler.toISOString(AVAILABLE_DATE))
        quizDto.setConclusionDate(DateHandler.toISOString(CONCLUSION_DATE))
        quizDto.setResultsDate(DateHandler.toISOString(RESULTS_DATE))
        quizDto.setSeries(1)
        quizDto.setVersion(VERSION)

        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        questionRepository.save(question)

        questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quizDto.setQuestions(questions)
    }

    @Unroll
    def "valid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quizDto'
        quizDto.setTitle(title)
        quizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        quizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        quizDto.setResultsDate(DateHandler.toISOString(resultsDate))
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(courseExecution.getId(), quizDto)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getScramble()
        result.isOneWay()
        result.isQrCodeOnly()
        result.getTitle() == title
        result.getCreationDate() != null
        result.getAvailableDate() == availableDate
        result.getConclusionDate() == conclusionDate
        if (resultsDate == null) {
            result.getResultsDate() == conclusionDate
        } else {
            result.getResultsDate() == resultsDate
        }
        result.getType() == quizType
        result.getSeries() == 1
        result.getVersion() == VERSION
        result.getQuizQuestions().size() == 1

        where:
        quizType                | title      | availableDate                 | conclusionDate                 | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | null                           | DateHandler.now().plusDays(2)
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | null                           | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | null                           | DateHandler.now().plusDays(2)
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(1)  | null
    }

    @Unroll
    def "invalid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate || errorMessage=#errorMessage "() {
        given: 'a quizDto'
        quizDto.setTitle(title)
        quizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        quizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        quizDto.setResultsDate(DateHandler.toISOString(resultsDate))
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(courseExecution.getId(), quizDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        quizRepository.count() == 0L

        where:
        quizType                | title      | availableDate                 | conclusionDate                 | resultsDate                    || errorMessage
        null                    | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_TYPE_FOR_QUIZ
        "   "                   | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_TYPE_FOR_QUIZ
        "Açores"                | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_TYPE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | null       | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | "        " | DateHandler.now()             | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | null                          | DateHandler.now().plusDays(1)  | DateHandler.now().plusDays(2)  || INVALID_AVAILABLE_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().minusDays(1) | DateHandler.now().plusDays(2)  || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | DateHandler.now()             | null                           | DateHandler.now().plusDays(1)  || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(2)  | DateHandler.now().plusDays(1)  || INVALID_RESULTS_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | DateHandler.now()             | DateHandler.now().plusDays(2)  | DateHandler.now().minusDays(1) || INVALID_RESULTS_DATE_FOR_QUIZ
    }

    def "create a TEACHER quiz wrong sequence"() {
        given: 'createQuiz a quiz'
        quizDto.setTitle(QUIZ_TITLE)
        quizDto.setType(Quiz.QuizType.GENERATED.toString())
        questionDto.setSequence(3)

        when:
        quizService.createQuiz(courseExecution.getId(), quizDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_QUESTION_SEQUENCE_FOR_QUIZ
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
