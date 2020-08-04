package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class DemoAuthTest extends SpockTest {

    User user
    Course course
    CourseExecution courseExecution


    def "demo student login" (){
        when:
        def result = authService.demoStudentAuth(false);

        then:
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    def "demo new student login" (){
        when:
        def result = authService.demoStudentAuth(true);

        then:
        result.user.name != DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    def "demo student login: invalid param" (){
        when:
        authService.demoStudentAuth(null);

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_PARAMETERS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
