package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

@DataJpaTest
class RemoveQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuizService quizService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def course
    def courseExecution
    def question
    def quiz
    def quizQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent("Question Content")

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        quizRepository.save(quiz)
        questionRepository.save(question)
    }

    def "remove a quiz"() {
        when:
        quizService.removeQuiz(quiz.getId())

        then: "the quiz is removed"
        quizRepository.count() == 0L
        quizQuestionRepository.count() == 0L
        questionRepository.count() == 1L
        question.getQuizQuestions().size() == 0
    }

    def "remove a qiz that has an answer"() {
        given: 'a quiz answer'
        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        quizService.removeQuiz(quiz.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_HAS_ANSWERS
        quizRepository.count() == 1L
        quizQuestionRepository.count() == 1L
        questionRepository.count() == 1L
        question.getQuizQuestions().size() == 1
    }

    @TestConfiguration
    static class TestContextConfiguration {

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
