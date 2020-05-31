package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class CreateQuizAnswerTest extends SpockTest {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    def setup() {

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user = new User('name', "username", User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
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
