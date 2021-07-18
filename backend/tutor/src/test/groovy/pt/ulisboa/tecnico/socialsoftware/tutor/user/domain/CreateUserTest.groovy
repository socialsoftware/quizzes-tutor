package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class CreateUserTest extends SpockTest {
    def user

    def "create User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, Role.STUDENT)

        then:
        result.getName() == USER_1_NAME
        result.getRole() == Role.STUDENT
    }

    def "create Tecnico User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == Role.STUDENT
    }

    def "create External User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == Role.STUDENT
    }

    def "create Demo User: name, username, email, role, state, admin" () {
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == Role.STUDENT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}