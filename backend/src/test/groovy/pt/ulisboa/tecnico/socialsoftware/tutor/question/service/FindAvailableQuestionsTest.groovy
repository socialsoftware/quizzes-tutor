package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

@DataJpaTest
class FindAvailableQuestionsTest extends SpockTest {
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())
    }

    @Unroll
    def "find multiple choice questions available (#status)"(Question.Status status) {
        given: "questions created for a given course"
        Question question = new MultipleChoiceQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        questionRepository.save(question)

        for (int i = 0; i < 5; i++) {
            question = new MultipleChoiceQuestion()
            question.setTitle("Question Title")
            question.setContent(QUESTION_1_CONTENT)
            question.setStatus(status)
            question.setCourse(course)
            questionRepository.save(question)
        }

        when:
        def result = questionService.findAvailableQuestions(course.getId())

        then: "the returned data are correct"
        result.size() == 1
        result.stream().allMatch({ q -> q instanceof MultipleChoiceQuestionDto })

        where:
        status << [Question.Status.DISABLED, Question.Status.REMOVED]
    }

    @Unroll
    def "find code fill questions available (#status)"(Question.Status status) {
        given: "#size questions created for a given course"
        Question question = new CodeFillInQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        question.setLanguage(CodeFillInQuestion.Language.Java)
        questionRepository.save(question)

        for (int i = 0; i < 5; i++) {
            question = new CodeFillInQuestion()
            question.setTitle("Question Title")
            question.setContent(QUESTION_1_CONTENT)
            question.setStatus(status)
            question.setCourse(course)
            question.setLanguage(CodeFillInQuestion.Language.Java)
            questionRepository.save(question)
        }

        when:
        def result = questionService.findAvailableQuestions(course.getId())

        then: "the returned data are correct"
        result.size() == 1
        result.stream().allMatch({ q -> q instanceof CodeFillInQuestionDto })

        where:
        status << [Question.Status.DISABLED, Question.Status.REMOVED]
    }

    @Unroll
    def "find available questions"() {
        given: "different type questions created for a given course"
        Question question = new CodeFillInQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        question.setLanguage(CodeFillInQuestion.Language.Java)
        questionRepository.save(question)

        question = new MultipleChoiceQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        questionRepository.save(question)

        when:
        def result = questionService.findAvailableQuestions(course.getId())

        then: "the returned data are correct"
        result.size() == 2
        result.stream().any {q -> q instanceof MultipleChoiceQuestionDto}
        result.stream().any {q -> q instanceof CodeFillInQuestionDto }
    }

    @Unroll
    def "find available questions in course without available questions"() {
        given: "different type questions created for a given course"
        Question question = new CodeFillInQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.DISABLED)
        question.setCourse(course)
        questionRepository.save(question)

        question = new MultipleChoiceQuestion()
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.REMOVED)
        question.setCourse(course)
        questionRepository.save(question)

        when:
        def result = questionService.findAvailableQuestions(course.getId())

        then: "the returned data are correct"
        result.size() == 0
    }

    @Unroll
    def "find available questions by invalid/non-existent courses (#courseId)"(int courseId) {
        when:
        def result = questionService.findAvailableQuestions(courseId)

        then:
        result.empty

        where:
        courseId << [-1,0,400]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
