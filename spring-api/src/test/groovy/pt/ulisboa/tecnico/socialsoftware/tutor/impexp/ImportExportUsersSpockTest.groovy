package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
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
        and: 'current year'
        def calendar = Calendar.getInstance();
        def year = calendar.get(Calendar.YEAR);

        when:
        userService.importUsers(usersXml)

        then:
        userRepository.findAll().size() == 2
        def userOne = userRepository.findByUsername('ar')
        userOne != null
        userOne.getNumber() == 1
        userOne.getName() == 'Rito'
        userOne.getRole().name() == 'TEACHER'
        userOne.getYear() == year

        def userTwo = userRepository.findByUsername('pc')
        userTwo != null
        userTwo.getNumber() == 2
        userTwo.getName() == 'Pedro'
        userTwo.getRole().name() == 'STUDENT'
        userTwo.getYear() == year
    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}
