package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser

@DataJpaTest
class ImportExportUsersTest extends SpockTest {
    def existingUsers

    def setup() {
        existingUsers = userRepository.findAll().size()

        User user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, false, AuthUser.Type.EXTERNAL)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        user = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, true, false, AuthUser.Type.EXTERNAL)
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
        userOne.getCourseExecutions().size() == 1
        userOne.getAuthUser().getEmail() == USER_1_EMAIL
        userOne.getAuthUser().getUsername() == USER_1_USERNAME

        def userTwo = userRepository.findByUsername(USER_2_USERNAME).orElse(null)
        userTwo != null
        userTwo.getKey() == existingUsers + 2
        userTwo.getName() == USER_2_NAME
        userTwo.getRole() == User.Role.STUDENT
        userTwo.getCourseExecutions().size() == 1
        userOne.getAuthUser().getEmail() == USER_1_EMAIL
        userOne.getAuthUser().getUsername() == USER_1_USERNAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
