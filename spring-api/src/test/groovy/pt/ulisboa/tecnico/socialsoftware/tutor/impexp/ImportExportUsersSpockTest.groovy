package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.service.UserService
import spock.lang.Specification

@DataJpaTest
class ImportExportUsersSpockTest extends Specification {
    @Autowired
    UserService userService

    @Autowired
    UserRepository userRepository

    def setup() {
        userService.create('Rito', 'ar', User.Role.TEACHER)
        userService.create('Pedro', 'pc', User.Role.STUDENT)
    }

    def 'export and import users'() {
        given: 'a xml with of users'
        def usersXml = userService.exportUsers()
        and: 'a clean database'
        userRepository.deleteAll()

        when:
        userService.importUsers(usersXml)

        then:
        userRepository.findAll().size() == 2
        userRepository.findByUsername('ar') != null
        userRepository.findByUsername('pc') != null
    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}
