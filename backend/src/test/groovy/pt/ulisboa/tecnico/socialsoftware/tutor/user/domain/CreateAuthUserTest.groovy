package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class CreateAuthUserTest  extends SpockTest {

    def User
    def authUser

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, false, AuthUser.Type.EXTERNAL)
        user.setKey(user.getId())
        userRepository.save(user)
    }

    def "create Auth User"() {
        when:
        authUser = new AuthUser(user)

        then:
        authUser.user.id == user.id
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
