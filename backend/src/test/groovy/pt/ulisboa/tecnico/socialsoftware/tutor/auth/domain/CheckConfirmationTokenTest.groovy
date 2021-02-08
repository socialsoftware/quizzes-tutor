package pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class CheckConfirmationTokenTest extends SpockTest {
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
    }

    def "checkConfirmationToken: correct token and date has not expired" (){
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        user.getAuthUser().checkConfirmationToken(USER_1_TOKEN)

        then:
        noExceptionThrown()
    }

    def "checkConfirmationToken: correct token but date has expired" (){
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_BEFORE)

        when:
        user.getAuthUser().checkConfirmationToken(USER_1_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.EXPIRED_CONFIRMATION_TOKEN
    }

    def "checkConfirmationToken: incorrect token" (){
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        user.getAuthUser().checkConfirmationToken(USER_2_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
