package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class ImportExportUsersTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def 'export and import with a AuthUser'() {
        given: 'two users with a auth user'
        def existingUsers = userRepository.findAll().size()
        User user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        def keyOne = user.getId()
        AuthUser authUser = user.getAuthUser()
        authUser.setPassword(USER_1_PASSWORD)
        authUser.setLastAccess(LOCAL_DATE_TODAY)

        user = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        def keyTwo = user.getId()
        AuthExternalUser authExternalUser = user.getAuthUser()
        authExternalUser.setPassword(USER_2_PASSWORD)
        authExternalUser.setLastAccess(LOCAL_DATE_TODAY)
        authExternalUser.setConfirmationToken(USER_2_TOKEN)
        authExternalUser.setTokenGenerationDate(LOCAL_DATE_TODAY)
        and: 'a xml with of users'
        def usersXml = userService.exportUsers()
        and: 'a clean database'
        userRepository.deleteAll()
        externalCourseExecution.getUsers().clear()

        when:
        userService.importUsers(usersXml)

        then:
        externalCourseExecution.getUsers().size() == 2
        and:
        userRepository.findAll().size() == existingUsers + 2
        def userOne = userRepository.findByKey(keyOne).orElse(null)
        userOne != null
        userOne.getKey() == keyOne
        userOne.getName() == USER_1_NAME
        userOne.getRole() == User.Role.TEACHER
        userOne.getCourseExecutions().size() == 1
        userOne.getAuthUser().getEmail() == USER_1_EMAIL
        userOne.getAuthUser().getUsername() == USER_1_USERNAME
        userOne.getAuthUser().getPassword() == USER_1_PASSWORD
        userOne.getAuthUser().getLastAccess() == LOCAL_DATE_TODAY
        userOne.getAuthUser().isActive()
        and:
        def userTwo = userRepository.findByKey(keyTwo).orElse(null)
        userTwo != null
        userTwo.getKey() == keyTwo
        userTwo.getName() == USER_2_NAME
        userTwo.getRole() == User.Role.STUDENT
        userTwo.getCourseExecutions().size() == 1
        userTwo.getAuthUser().getEmail() == USER_2_EMAIL
        userTwo.getAuthUser().getUsername() == USER_2_USERNAME
        userTwo.getAuthUser().getPassword() == USER_2_PASSWORD
        userTwo.getAuthUser().getLastAccess() == LOCAL_DATE_TODAY
        ((AuthExternalUser)userTwo.getAuthUser()).getConfirmationToken() == USER_2_TOKEN
        ((AuthExternalUser)userTwo.getAuthUser()).getTokenGenerationDate() == LOCAL_DATE_TODAY
        !userTwo.getAuthUser().isActive()
    }

    def 'export and import users without a AuthUser'() {
        given: 'two users without a auth user'
        def existingUsers = userRepository.findAll().size()
        User user = new User(USER_1_NAME, User.Role.TEACHER, false)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        def keyOne = user.getId()

        user = new User(USER_2_NAME, User.Role.STUDENT, false)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        def keyTwo = user.getId()
        and: 'a xml with of users'
        def usersXml = userService.exportUsers()
        and: 'a clean database'
        userRepository.deleteAll()
        externalCourseExecution.getUsers().clear()

        when:
        userService.importUsers(usersXml)

        then:
        externalCourseExecution.getUsers().size() == 2

        userRepository.findAll().size() == existingUsers + 2
        def userOne = userRepository.findByKey(keyOne).orElse(null)
        userOne != null
        userOne.getKey() == keyOne
        userOne.getName() == USER_1_NAME
        userOne.getRole() == User.Role.TEACHER
        userOne.getCourseExecutions().size() == 1

        def userTwo = userRepository.findByKey(keyTwo).orElse(null)
        userTwo != null
        userTwo.getKey() == keyTwo
        userTwo.getName() == USER_2_NAME
        userTwo.getRole() == User.Role.STUDENT
        userTwo.getCourseExecutions().size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
