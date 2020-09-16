package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateAuthUserTest  extends SpockTest {

    def user
    def authUser

    def setup() {
        user = new User(USER_1_NAME, User.Role.STUDENT, false)
        userRepository.save(user)
        user.setKey(user.getId())
    }

    @Unroll
    def "create auth user type=#type | active=#active" () {
        authUser = AuthUser.createAuthUser(user, USER_1_USERNAME, USER_1_EMAIL, type)
        authUserRepository.save(authUser)

        when:
        authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()

        then:
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser.isActive().equals(active)
        and: "the user and authUser are connected"
        authUser.user != null
        authUser.user.equals(user)

        and:" is if the correct type"
        switch (type) {
            case AuthUser.Type.TECNICO:
                authUser instanceof AuthTecnicoUser
                break
            case AuthUser.Type.EXTERNAL:
                authUser instanceof AuthExternalUser
                break
            case AuthUser.Type.DEMO:
                authUser instanceof AuthDemoUser
                break
        }

        where:
        type                    | active
        AuthUser.Type.TECNICO   | true
        AuthUser.Type.EXTERNAL  | false
        AuthUser.Type.DEMO      | true



    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
