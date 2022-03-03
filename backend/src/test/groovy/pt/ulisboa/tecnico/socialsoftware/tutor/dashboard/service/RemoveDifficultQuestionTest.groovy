package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class RemoveDifficultQuestionTest extends SpockTest {
    def student
    def dashboard
    def question

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        question = new Question()

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "student removes a difficult question from the dashboard"() {
        given: "a difficult question"
        def difficultQuestion = new DifficultQuestion()
        difficultQuestion.setDashboard(dashboard)
        difficultQuestion.setQuestion(question)
        difficultQuestion.setPercentage(25)
        difficultQuestion.setCollected(DateHandler.now().minusDays(7))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.removeDifficultQuestion(difficultQuestion.getId())

        then: "it is removed from the database"
        difficultQuestionRepository.count() == 0
        and: "and it is not referenced from the dashboard"
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getDifficultQuestions().size() == 0
    }

    def "student removes a difficult question and one week later it is appears again"() {
        // Student removes difficult question
        // Advance time and check the database
        expect: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}