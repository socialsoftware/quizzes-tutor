package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class ImportExportAnswersSpockTest extends Specification {
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
    def quizAnswer
    def answerDate
    def timeTaken
    def quizQuestion
    def formatter

    @Autowired
    UserService userService

    @Autowired
    QuizService quizService

    @Autowired
    QuestionService questionService

    @Autowired
    AnswerService answerService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        def course = new Course(COURSE_NAME)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def optionDto = new OptionDto()
        optionDto.setSequence(0)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        def question = questionService.createQuestion(COURSE_NAME, questionDto)

        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now()
        quizDto.setCreationDate(creationDate.format(formatter))
        quizDto.setAvailableDate(availableDate.format(formatter))
        quizDto.setConclusionDate(conclusionDate.format(formatter))
        quizDto.setType(Quiz.QuizType.EXAM)
        quizDto.setSeries(1)
        quizDto.setVersion(VERSION)
        quiz = quizService.createQuiz(courseExecution.getId(), quizDto)

        quizQuestion = quizService.addQuestionToQuiz(question.getId(), quiz.getId())

        def user = userService.createUser('Pedro', 'pc', User.Role.STUDENT)

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
        quizAnswerResult.getQuiz().getKey() == 1
        questionAnswerRepository.findAll().size() == 1
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)
        questionAnswerResult.getTimeTaken() == timeTaken
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
