package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class CreateAuthUserTest  extends SpockTest {

    User user
    AuthUser authUser

    def setup() {
        user = new User(USER_1_NAME, User.Role.STUDENT, false)
        userRepository.save(user)
        user.setKey(user.getId())
    }

    def "create AuthTecnicoUser"() {
        authUser = new AuthTecnicoUser(user, USER_1_USERNAME, USER_1_EMAIL)
        authUserRepository.save(authUser)

        when:
        authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()

        then:
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser instanceof  AuthTecnicoUser
    }

    def "create AuthExternalUser"() {
        authUser = new AuthExternalUser(user, USER_1_USERNAME, USER_1_EMAIL)
        authUserRepository.save(authUser)

        when:
        authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()

        then:
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser instanceof  AuthExternalUser
    }

    def "create AuthDemoUser"() {
        authUser = new AuthDemoUser(user, USER_1_USERNAME, USER_1_EMAIL)
        authUserRepository.save(authUser)

        when:
        authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()

        then:
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser instanceof  AuthDemoUser
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
