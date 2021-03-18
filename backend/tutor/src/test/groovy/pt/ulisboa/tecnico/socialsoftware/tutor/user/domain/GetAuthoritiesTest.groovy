package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest{
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
    }

    @Unroll
    def "get user authorities" (){
        given:
        user.setAdmin(admin)
        user.setRole(role)

        when:
        def result  = user.getAuthUser().getAuthorities()

        then:
        result.size() == size
        def iter = result.iterator()
        iter.next().getAuthority() == firstAuthority
        if (result.size() > 1)
            iter.next().getAuthority() == secondAuthority

        where:
        admin  | role                   || size | firstAuthority  | secondAuthority
        false  | User.Role.STUDENT      || 1    | ROLE_STUDENT    | null
        false  | User.Role.TEACHER      || 1    | ROLE_TEACHER    | null
        false  | User.Role.ADMIN        || 1    | ROLE_ADMIN      | null
        false  | User.Role.DEMO_ADMIN   || 1    | ROLE_DEMO_ADMIN | null
        true   | User.Role.STUDENT      || 2    | ROLE_STUDENT    | ROLE_ADMIN
        true   | User.Role.TEACHER      || 2    | ROLE_TEACHER    | ROLE_ADMIN
        true   | User.Role.ADMIN        || 2    | ROLE_ADMIN      | ROLE_ADMIN
        true   | User.Role.DEMO_ADMIN   || 2    | ROLE_DEMO_ADMIN | ROLE_ADMIN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
