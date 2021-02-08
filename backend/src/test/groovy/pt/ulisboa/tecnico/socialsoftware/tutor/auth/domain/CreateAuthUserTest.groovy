package pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateAuthUserTest  extends SpockTest {

    def user
    def authUser

    def setup() {
        user = new User(USER_1_NAME, User.Role.STUDENT, false)
        userRepository.save(user)
    }

    @Unroll
    def "create auth user type=#type | active=#active | type=#type" () {
        given:
        authUser = AuthUser.createAuthUser(user, USER_1_USERNAME, USER_1_EMAIL, type)
        authUserRepository.save(authUser)

        when:
        def objType = createClassAutUser(type).getClass()

        then:
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser.isActive().equals(active)
        and: "the user and authUser are connected"
        authUser.user != null
        authUser.user.equals(user)

        objType.isInstance(authUser)

        where:
        type                    | active
        AuthUser.Type.TECNICO   | true
        AuthUser.Type.EXTERNAL  | false
        AuthUser.Type.DEMO      | true
    }

    def createClassAutUser(AuthUser.Type type) {
        switch (type) {
            case AuthUser.Type.TECNICO: return new AuthTecnicoUser()
            case AuthUser.Type.EXTERNAL: return new AuthExternalUser()
            case AuthUser.Type.DEMO: return new AuthDemoUser()
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
