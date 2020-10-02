package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class CreateUserTest extends SpockTest {
    def user

    def "create User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, User.Role.STUDENT, false)

        then:
        result.getName() == USER_1_NAME
        result.getRole() == User.Role.STUDENT
        !result.isAdmin()
    }

    def "create Tecnico User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.STUDENT
        !result.isAdmin()
        result.getAuthUser() != null
        result.getAuthUser().getUsername() == USER_1_USERNAME
        result.getAuthUser().getEmail() == USER_1_EMAIL
        result.getAuthUser() instanceof AuthTecnicoUser
    }

    def "create External User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.STUDENT
        !result.isAdmin()
        result.getAuthUser() != null
        result.getAuthUser().getUsername() == USER_1_USERNAME
        result.getAuthUser().getEmail() == USER_1_EMAIL
        result.getAuthUser() instanceof AuthExternalUser
        !result.getAuthUser().isActive()
    }

    def "create Demo User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.DEMO)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.STUDENT
        !result.isAdmin()
        result.getAuthUser() != null
        result.getAuthUser().getUsername() == USER_1_USERNAME
        result.getAuthUser().getEmail() == USER_1_EMAIL
        result.getAuthUser() instanceof AuthDemoUser
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}