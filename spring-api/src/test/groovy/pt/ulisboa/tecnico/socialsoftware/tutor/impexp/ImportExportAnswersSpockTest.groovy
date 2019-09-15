package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class ImportExportAnswersSpockTest extends Specification {
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

    def quiz
    def creationDate
    def availableDate
    def conclusionDate
    def quizAnswer
    def answerDate
    def timeTaken
    def quizQuestion

    @Autowired
    UserService userService

    @Autowired
    QuizService quizService

    @Autowired
    QuestionService questionService

    @Autowired
    AnswerService answerService

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def setup() {
        def question = new QuestionDto()
        question.setNumber(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)
        def option = new OptionDto()
        option.setNumber(0)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(option)
        question.setOptions(options)
        question = questionService.createQuestion(question)

        def quizDto = new QuizDto()
        quizDto.setNumber(1)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now()
        quizDto.setCreationDate(creationDate)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setYear(2019)
        quizDto.setType(Quiz.QuizType.EXAM.name())
        quizDto.setSeries(1)
        quizDto.setVersion(VERSION)
        quiz = quizService.createQuiz(quizDto)

        quizQuestion = quizService.addQuestionToQuiz(question.getId(), quiz.getId())

        def user = userService.create('Pedro', 'pc', User.Role.STUDENT)

        quizAnswer = answerService.createQuizAnswer(user.getId(), quiz.getId())

        def answersDto = new ArrayList<ResultAnswerDto>()
        def answerDto = new ResultAnswerDto()
        answerDto.setQuizQuestionId(quizQuestion.getId())
        def optionId = question.getOptions().stream().findAny().orElse(null).id
        answerDto.setOptionId(optionId)
        timeTaken = 0
        answerDto.setTimeTaken(timeTaken)
        answersDto.add(answerDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswers(answersDto)
        answerDate = LocalDateTime.now().plusHours(1)
        resultAnswersDto.setAnswerDate(answerDate)
        answerService.submitQuestionsAnswers(user, resultAnswersDto)
    }

    def 'export and import answers'() {
        given: 'a xml with a quiz'
        def answersXml = answerService.exportAnswers()
        and: 'delete answers'
        answerService.removeQuizAnswer(quizAnswer.getId())

        when:
        answerService.importAnswers(answersXml)

        then:
        quizAnswerRepository.findAll().size() == 1
        def quizAnswerResult = quizAnswerRepository.findAll().get(0)
        quizAnswerResult.getAnswerDate() == answerDate
        quizAnswerResult.getCompleted()
        quizAnswerResult.getUser().getUsername() == 'pc'
        quizAnswerResult.getQuiz().getNumber() == 1
        quizAnswerResult.getQuestionAnswers().size() == 1
        def questionAnswerResult = quizAnswerResult.getQuestionAnswers().stream().findAny().orElse(null)
        questionAnswerResult.getQuizAnswer() == quizAnswerResult
        questionAnswerResult.getTimeTaken() == timeTaken
        questionAnswerResult.getQuizQuestion() == quizQuestion
        questionAnswerResult.getOption().getNumber() == 0
    }

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }
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
    }

}
