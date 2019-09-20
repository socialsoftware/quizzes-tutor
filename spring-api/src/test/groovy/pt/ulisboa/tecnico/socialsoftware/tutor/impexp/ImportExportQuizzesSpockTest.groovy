package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class ImportExportQuizzesSpockTest extends Specification {
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
    QuestionService questionService

    @Autowired
    QuizRepository quizRepository

    def setup() {
        def question = new QuestionDto()
        question.setNumber(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)
        def option = new OptionDto()
        option.setNumber(1)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(option)
        question.setOptions(options)
        question = questionService.createQuestion(question)

        def quizDto = new QuizDto()
        quizDto.setNumber(1)
        quizDto.setScramble(false)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(2)
        quizDto.setCreationDate(creationDate)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setYear(2019)
        quizDto.setType(Quiz.QuizType.EXAM.name())
        quizDto.setSeries(1)
        quizDto.setVersion(VERSION)
        quiz = quizService.createQuiz(quizDto)

        quizService.addQuestionToQuiz(question.getId(), quiz.getId())
    }

    def 'export and import quizzes'() {
        given: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzes()
        and: 'delete quiz and quizQuestion'
        quizService.removeQuiz(quiz.getId())

        when:
        quizService.importQuizzes(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 1
        def quizResult = quizRepository.findAll().get(0)
        quizResult.getNumber() == 1
        !quizResult.getScramble()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getYear() == 2019
        quizResult.getType() == Quiz.QuizType.EXAM.name()
        quizResult.getSeries() == 1
        quizResult.getVersion() == VERSION
        quizResult.getQuizQuestions().size() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getNumber() == 1
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
    }

}
