package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

@DataJpaTest
class ImportExportQuizzesTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

    def quiz
    def creationDate
    def availableDate
    def conclusionDate

    @Autowired
    QuizService quizService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionService questionService

    @Autowired
    QuizRepository quizRepository

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def optionDto = new OptionDto()
        optionDto.setSequence(1)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        questionDto = questionService.createQuestion(course.getId(), questionDto)

        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(false)
        quizDto.setQrCodeOnly(true)
        quizDto.setOneWay(false)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = DateHandler.now()
        availableDate = DateHandler.now()
        conclusionDate = DateHandler.now().plusDays(2)
        quizDto.setCreationDate(DateHandler.toISOString(creationDate))
        quizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        quizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        quizDto.setType(Quiz.QuizType.EXAM.toString())
        quizDto.setSeries(1)
        quizDto.setVersion(VERSION)
        quiz = quizService.createQuiz(courseExecution.getId(), quizDto)

        quizService.addQuestionToQuiz(questionDto.getId(), quiz.getId())
    }

    def 'export and import quizzes'() {
        given: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzesToXml()
        and: 'delete quiz and quizQuestion'
        quizService.removeQuiz(quiz.getId())

        when:
        quizService.importQuizzesFromXml(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 1
        def quizResult = quizRepository.findAll().get(0)
        quizResult.getKey() == 1
        !quizResult.getScramble()
        quizResult.isQrCodeOnly()
        !quizResult.isOneWay()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getType() == Quiz.QuizType.EXAM
        quizResult.getSeries() == 1
        quizResult.getVersion() == VERSION
        quizResult.getQuizQuestions().size() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getKey() == 1
    }

    def 'export quiz to latex'() {
        when:
        def quizzesLatex = quizService.exportQuizzesToLatex(quiz.getId())

        then:
        quizzesLatex != null
    }

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
