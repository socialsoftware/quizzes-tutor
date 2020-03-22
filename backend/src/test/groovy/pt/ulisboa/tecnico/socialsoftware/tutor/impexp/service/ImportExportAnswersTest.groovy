package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class ImportExportAnswersTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer TIME_TAKEN = 1234
    public static final Integer SEQUENCE = 0
    public static final LocalDateTime ANSWER_DATE = LocalDateTime.now()


    @Autowired
    AnswerService answerService

    @Autowired
    UserService userService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

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

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    def quizAnswer
    def questionAnswer

    def setup() {

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        def option = new Option()
        option.setSequence(0)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        optionRepository.save(option)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        quiz.setCreationDate(LocalDateTime.now())
        quiz.setAvailableDate(LocalDateTime.now())
        quiz.setConclusionDate(LocalDateTime.now())
        quiz.setType(Quiz.QuizType.EXAM)
        quiz.setSeries(1)
        quiz.setVersion(VERSION)
        quizRepository.save(quiz)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(SEQUENCE)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        def user = userService.createUser('Pedro', 'pc', User.Role.STUDENT)

        quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(user)
        quizAnswer.setAnswerDate(ANSWER_DATE)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setOption(option)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
    }

    def 'export and import answers'() {
        given: 'a xml with a quiz'
        def answersXml = answerService.exportAnswers()
        System.out.println(answersXml)
        and: 'delete answers'
        quizAnswerRepository.deleteAll()
        questionAnswerRepository.deleteAll()

        when:
        answerService.importAnswers(answersXml)

        then:
        quizAnswerRepository.findAll().size() == 1
        def quizAnswerResult = quizAnswerRepository.findAll().get(0)
        quizAnswerResult.getAnswerDate() == ANSWER_DATE
        quizAnswerResult.isCompleted()
        quizAnswerResult.getUser().getUsername() == 'pc'
        quizAnswerResult.getQuiz().getKey() == 1
        questionAnswerRepository.findAll().size() == 1
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)
        questionAnswerResult.getTimeTaken() == TIME_TAKEN
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
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}
