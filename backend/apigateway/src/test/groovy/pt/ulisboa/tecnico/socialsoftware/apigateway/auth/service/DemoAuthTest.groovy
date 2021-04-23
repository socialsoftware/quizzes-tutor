package pt.ulisboa.tecnico.socialsoftware.apigateway.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.apigateway.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTest
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role

@DataJpaTest
class DemoAuthTest extends SpockTest {

    def "demo admin login" (){
        when:
        def result = authUserService.demoAdminAuth();

        then:
        result.user.name == DEMO_ADMIN_NAME
        result.user.role == Role.DEMO_ADMIN
    }

    def "demo teacher login" (){
        when:
        def result = authUserService.demoTeacherAuth();

        then:
        result.user.name == DEMO_TEACHER_NAME
        result.user.role == Role.TEACHER
    }

    def "demo student login" (){
        when:
        def result = authUserService.demoStudentAuth(false);

        then:
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == Role.STUDENT
    }

    def "demo new student login" (){
        when:
        def result = authUserService.demoStudentAuth(true);

        then:
        result.user.name != DEMO_STUDENT_NAME
        result.user.role == Role.STUDENT
    }

    def "demo student login: invalid param" (){
        when:
        def result = authUserService.demoStudentAuth(null);

        then:
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == Role.STUDENT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
