package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class CreateQuizAnswerTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()

        User user = new User(USER_1_NAME, User.Role.STUDENT, false)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

    }

    def 'create a quiz answer' () {
        given:
        def userId = userRepository.findAll().get(0).getId()
        def quizId = quizRepository.findAll().get(0).getId()

        when:
        answerService.createQuizAnswer(userId, quizId)

        then:
        quizAnswerRepository.findAll().size() == 1
        def quizAnswer = quizAnswerRepository.findAll().get(0)
        quizAnswer.getId() != null
        !quizAnswer.isCompleted()
        quizAnswer.getUser().getId() == userId
        quizAnswer.getUser().getQuizAnswers().contains(quizAnswer)
        quizAnswer.getQuiz().getId() == quizId
        quizAnswer.getQuiz().getQuizAnswers().contains(quizAnswer)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
