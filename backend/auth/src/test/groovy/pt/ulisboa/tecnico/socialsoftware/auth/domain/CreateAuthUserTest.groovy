package pt.ulisboa.tecnico.socialsoftware.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.SpockTest
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import spock.lang.Unroll

@DataJpaTest
class CreateAuthUserTest  extends SpockTest {

    def authUser

    @Unroll
    def "create auth user type=#type | active=#active | type=#type" () {
        given:
        authUser = authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, type)
        authUserRepository.save(authUser)

        when:
        def objType = createClassAutUser(type).getClass()

        then:
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser.isActive().equals(active)
        and: "the user and authUser are connected"
        authUser.getUserSecurityInfo() != null
        def user = userRepository.findAll().get(0)
        user.getId() == authUser.getUserSecurityInfo().getId()
        user.getName() == authUser.getUserSecurityInfo().getName()
        userRepository.deleteAll()

        objType.isInstance(authUser)

        where:
        type                    | active
        AuthUserType.TECNICO  | true
        AuthUserType.EXTERNAL | false
        AuthUserType.DEMO     | true
    }

    def createClassAutUser(AuthUserType type) {
        switch (type) {
            case AuthUserType.TECNICO: return new AuthTecnicoUser()
            case AuthUserType.EXTERNAL: return new AuthExternalUser()
            case AuthUserType.DEMO: return new AuthDemoUser()
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
