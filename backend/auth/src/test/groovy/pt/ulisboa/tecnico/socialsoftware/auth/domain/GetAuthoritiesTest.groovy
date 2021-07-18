package pt.ulisboa.tecnico.socialsoftware.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.SpockTest
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest {
    def authUser

    def setup() {
        authUser = authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUserType.EXTERNAL)
        authUserRepository.save(authUser)
    }

    @Unroll
    def "get user authorities" (){
        given:
        authUser.getUserSecurityInfo().setAdmin(admin)
        authUser.getUserSecurityInfo().setRole(role)

        when:
        def result  = authUserRepository.findAuthUserByUsername(authUser.getUsername()).get().getAuthorities()

        then:
        result.size() == size
        def iter = result.iterator()
        iter.next().getAuthority() == firstAuthority
        if (result.size() > 1)
            iter.next().getAuthority() == secondAuthority

        where:
        admin  | role                   || size | firstAuthority  | secondAuthority
        false  | Role.STUDENT    || 1 | ROLE_STUDENT    | null
        false  | Role.TEACHER    || 1 | ROLE_TEACHER    | null
        false  | Role.ADMIN      || 1 | ROLE_ADMIN      | null
        false  | Role.DEMO_ADMIN || 1 | ROLE_DEMO_ADMIN | null
        true   | Role.STUDENT    || 2 | ROLE_STUDENT    | ROLE_ADMIN
        true   | Role.TEACHER    || 2 | ROLE_TEACHER    | ROLE_ADMIN
        true   | Role.ADMIN      || 2 | ROLE_ADMIN      | ROLE_ADMIN
        true   | Role.DEMO_ADMIN || 2 | ROLE_DEMO_ADMIN | ROLE_ADMIN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
