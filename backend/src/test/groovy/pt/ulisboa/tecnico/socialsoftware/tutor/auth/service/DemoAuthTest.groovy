package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class DemoAuthTest extends SpockTest {

    def "demo admin login" (){
        when:
        def result = authUserService.demoAdminAuth();

        then:
        result.user.name == DEMO_ADMIN_NAME
        result.user.role == User.Role.DEMO_ADMIN
    }

    def "demo teacher login" (){
        when:
        def result = authUserService.demoTeacherAuth();

        then:
        result.user.name == DEMO_TEACHER_NAME
        result.user.role == User.Role.TEACHER
    }

    def "demo student login" (){
        when:
        def result = authUserService.demoStudentAuth(false);

        then:
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    def "demo new student login" (){
        when:
        def result = authUserService.demoStudentAuth(true);

        then:
        result.user.name != DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    def "demo student login: invalid param" (){
        when:
        def result = authUserService.demoStudentAuth(null);

        then:
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
