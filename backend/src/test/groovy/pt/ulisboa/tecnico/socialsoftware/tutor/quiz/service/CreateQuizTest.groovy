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
    public static final String TODAY = DateHandler.toISOString(DateHandler.now())
    public static final String TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    public static final String LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))

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
        quizDto.setAvailableDate(TODAY)
        quizDto.setConclusionDate(TOMORROW)
        quizDto.setResultsDate(LATER)
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
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
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
        result.getAvailableDate() == DateHandler.toLocalDateTime(availableDate)
        result.getConclusionDate() == DateHandler.toLocalDateTime(conclusionDate)
        if (resultsDate == null) {
            result.getResultsDate() == DateHandler.toLocalDateTime(conclusionDate)
        } else {
            result.getResultsDate() == DateHandler.toLocalDateTime(resultsDate)
        }
        result.getType() == quizType
        result.getSeries() == 1
        result.getVersion() == VERSION
        result.getQuizQuestions().size() == 1

        where:
        quizType                | title      | availableDate               | conclusionDate | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TODAY                       | TOMORROW       | LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | "2020-04-22T02:03:00+01:00" | TOMORROW       | LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TODAY                       | null           | LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TODAY                       | null           | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TODAY                       | null           | LATER
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | TODAY                       | TOMORROW       | null
    }

    @Unroll
    def "invalid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate || errorMessage=#errorMessage "() {
        given: 'a quizDto'
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(courseExecution.getId(), quizDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        quizRepository.count() == 0L

        where:
        quizType                | title      | availableDate | conclusionDate     | resultsDate        || errorMessage
        null                    | QUIZ_TITLE | TODAY         | TOMORROW           | LATER              || INVALID_TYPE_FOR_QUIZ
        "   "                   | QUIZ_TITLE | TODAY         | TOMORROW           | LATER              || INVALID_TYPE_FOR_QUIZ
        "Açores"                | QUIZ_TITLE | TODAY         | TOMORROW           | LATER              || INVALID_TYPE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | null       | TODAY         | TOMORROW           | LATER              || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | "        " | TODAY         | TOMORROW           | LATER              || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | null          | TOMORROW           | LATER              || INVALID_AVAILABLE_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TOMORROW      | TODAY              | LATER              || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | TODAY         | null               | TOMORROW           || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TODAY         | LATER              | TOMORROW           || INVALID_RESULTS_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | TOMORROW      | LATER              | TODAY              || INVALID_RESULTS_DATE_FOR_QUIZ
    }

    def "create quiz with wrong question sequence"() {
        given: 'createQuiz a quiz'
        quizDto.setTitle(QUIZ_TITLE)
        quizDto.setType(Quiz.QuizType.GENERATED.toString())
        questionDto.setSequence(3)

        when:
        quizService.createQuiz(courseExecution.getId(), quizDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == INVALID_QUESTION_SEQUENCE_FOR_QUIZ
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
