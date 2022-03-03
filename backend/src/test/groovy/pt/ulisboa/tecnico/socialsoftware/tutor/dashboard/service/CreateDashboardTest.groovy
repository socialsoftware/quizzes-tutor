package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import spock.lang.Shared
import spock.lang.Unroll


@DataJpaTest
class CreateDashboardTest extends SpockTest {

    def student

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, false)
        userRepository.save(student)
    }


    def "create an empty dashboard"() {
        given: "a student in a course execution"
        student.addCourse(externalCourseExecution)

        when: "a dashboard is created"
        dashboardService.createDashboard(externalCourseExecution.getId(), student.getId())

        then: "an empty dashboard is created"
        dashboardRepository.count() == 1L
        def result = dashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getLastCheckFailedAnswers() != null
        result.getLastCheckDifficultAnswers() != null
        result.getLastCheckFailedAnswers() == result.getLastCheckDifficultAnswers()
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getStudent().getId() == student.getId()

        and: "the student has a reference for the dashboard"
        student.getDashboards().size() == 1
        student.getDashboards().contains(result)
    }

    def "cannot create multiple dashboards for a student on a course execution"() {
        given: "a student in a course execution"
        student.addCourse(externalCourseExecution)

        and: "an empty dashboard for the student"
        dashboardService.createDashboard(externalCourseExecution.getId(), student.getId())

        when: "a second dashboard is created"
        dashboardService.createDashboard(externalCourseExecution.getId(), student.getId())

        then: "exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_ALREADY_HAS_DASHBOARD
    }

    def "cannot create a dashboard for a user that does not belong to the course execution"() {
        when: "a dashboard is created"
        dashboardService.createDashboard(externalCourseExecution.getId(), student.getId())

        then: "exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot create a dashboard with courseExecutionId=#courseExecutionId | studentId=#studentId"() {
        when: "a dashboard is created"
        dashboardService.createDashboard(courseExecutionId, studentId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        courseExecutionId       | studentId         || errorMessage
        null                    | 1                 || ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        1                       | null              || ErrorMessage.USER_NOT_FOUND
        100                     | 1                 || ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        1                       | 100               || ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}