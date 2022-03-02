package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll

@DataJpaTest
class GetDashboardTest extends SpockTest {
    def authUserDto
    def courseExecutionDto

    def setup() {
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
    }

    def "get a dashboard when dashboard does not exist"() {
        when: "getting a dashboard"
        dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "an empty dashboard is created"
        dashboardRepository.count() == 1L
        def result = dashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getLastCheckFailedAnswers() != null
        result.getLastCheckDifficultQuestions() != null
        result.getLastCheckFailedAnswers() == result.getLastCheckDifficultQuestions()
        result.getCurrentWeek() != null
        result.getCourseExecution().getId() == courseExecutionDto.getCourseExecutionId()
        result.getStudent().getId() == authUserDto.getId()

        and: "the student has a reference for the dashboard"
        def student = userRepository.getById(authUserDto.getId())
        student.getDashboards().size() == 1
        student.getDashboards().contains(result)
    }

    def "get a dashboard and it already exists"() {
        given: "an empty dashboard for the student"
        def dashboardDto = dashboardService.createDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        when: "a second dashboard is created"
        def getDashboardDto = dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "it is the same dashboard"
        dashboardDto.getId() == getDashboardDto.getId()
    }

    def "cannot get a dashboard for a user that does not belong to the course execution"() {
        given: "another course execution"
        createExternalCourseAndExecution()

        when: "get a dashboard"
        dashboardService.getDashboard(externalCourseExecution.getId(), authUserDto.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot create a dashboard with courseExecutionId=#courseExecutionId | studentId=#studentId"() {
        when: "get a dashboard"
        dashboardService.getDashboard(courseExecutionId, studentId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        courseExecutionId       | studentId         || errorMessage
        0                       | 1                 || ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        1                       | 0                 || ErrorMessage.USER_NOT_FOUND
        100                     | 1                 || ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        1                       | 100               || ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}