package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import com.google.gson.JsonObject
import net.minidev.json.parser.JSONParser
import org.fenixedu.sdk.Authorization
import org.fenixedu.sdk.FenixEduUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.FenixEduInterface
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthService
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.FenixAuthenticationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class FenixAuthServiceSpockTest extends Specification {
    public static final String IST12628 = "ist12628"
    public static final String NAME = "NAME"

    @Autowired
    AuthService authService

    def client
    def courses

    def setup() {
        client = Mock(FenixEduInterface)

        courses = new ArrayList<>()
        def courseDto = new CourseDto("Arquitecturas de Software", "ASof7", "1º Semestre 2019/2020")
        courses.add(courseDto)
        courseDto = new CourseDto("Tópicos Avançados em Engenharia de Software", "TAES", "1º Semestre 2019/2020")
        courses.add(courseDto)
    }

    def "teacher auth no user in db"() {
        given:
        client.getPersonName() >> NAME
        client.getPersonUsername() >> IST12628
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == IST12628
    }

    @TestConfiguration
    static class AuthServiceImplTestContextConfiguration {

        @Bean
        AuthService authService() {
            return new AuthService()
        }

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        LogService logService() {
            return new LogService()
        }
    }

}
