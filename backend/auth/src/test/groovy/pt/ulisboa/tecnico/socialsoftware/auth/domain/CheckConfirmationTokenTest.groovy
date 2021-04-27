package pt.ulisboa.tecnico.socialsoftware.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.SpockTest
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException

@DataJpaTest
class CheckConfirmationTokenTest extends SpockTest {
    def authUser

    def setup() {
        authUser = authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.EXTERNAL)
        /*user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, false)
        userRepository.save(user)*/
    }

    def "checkConfirmationToken: correct token and date has not expired" (){
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        authUser.checkConfirmationToken(USER_1_TOKEN)

        then:
        noExceptionThrown()
    }

    def "checkConfirmationToken: correct token but date has expired" (){
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(LOCAL_DATE_BEFORE)

        when:
        authUser.checkConfirmationToken(USER_1_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.EXPIRED_CONFIRMATION_TOKEN
    }

    def "checkConfirmationToken: incorrect token" (){
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        authUser.checkConfirmationToken(USER_2_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
