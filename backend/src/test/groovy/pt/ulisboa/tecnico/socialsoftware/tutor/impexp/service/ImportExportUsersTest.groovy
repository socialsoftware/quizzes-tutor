package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class ImportExportUsersTest extends SpockTest {
    def existingUsers

    def setup() {
        existingUsers = userRepository.findAll().size()

        User user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.TEACHER)
        user.setEmail(USER_1_EMAIL)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        user = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user.setEmail(USER_2_EMAIL)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        user.setKey(user.getId())
    }

    def 'export and import users'() {
        given: 'a xml with of users'
        def usersXml = userService.exportUsers()
        and: 'a clean database'
        userRepository.deleteAll()
        externalCourseExecution.getUsers().clear()

        when:
        userService.importUsers(usersXml)

        then:
        externalCourseExecution.getUsers().size() == 2

        userRepository.findAll().size() == existingUsers + 2
        def userOne = userRepository.findByUsername(USER_1_USERNAME).orElse(null)
        userOne != null
        userOne.getKey() == existingUsers + 1
        userOne.getName() == USER_1_NAME
        userOne.getRole() == User.Role.TEACHER
        userOne.getEmail() == USER_1_EMAIL
        userOne.getCourseExecutions().size() == 1

        def userTwo = userRepository.findByUsername(USER_2_USERNAME).orElse(null)
        userTwo != null
        userTwo.getKey() == existingUsers + 2
        userTwo.getName() == USER_2_NAME
        userTwo.getRole() == User.Role.STUDENT
        userTwo.getEmail() == USER_2_EMAIL
        userTwo.getCourseExecutions().size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
