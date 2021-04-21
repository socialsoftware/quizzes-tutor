package pt.ulisboa.tecnico.socialsoftware.apigateway.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.events.AddCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.DeleteAuthUserEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.USER_NOT_FOUND;

@Component
public class AuthUserSubscriptions {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Subscribe
    public void addCourseExecution(AddCourseExecutionEvent event) {
        AuthUser authUser = authUserRepository.findAuthUserById(event.getUserId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, event.getUserId()));

        authUser.addCourseExecution(event.getCourseExecutionId());
    }

    @Subscribe
    public void deleteAuthUser(DeleteAuthUserEvent event) {
        AuthUser authUser = authUserRepository.findAuthUserById(event.getUserId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, event.getUserId()));

        authUser.remove();
        authUserRepository.delete(authUser);
    }
}
