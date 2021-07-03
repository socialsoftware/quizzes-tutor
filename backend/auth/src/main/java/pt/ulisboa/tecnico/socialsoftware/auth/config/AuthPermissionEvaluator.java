package pt.ulisboa.tecnico.socialsoftware.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.auth.services.remote.AuthUserRequiredService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;

import java.io.Serializable;

@Component
public class AuthPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private AuthUserRequiredService authRequiredService;

    private static final Logger logger = LoggerFactory.getLogger(AuthPermissionEvaluator.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        UserInfo userInfo = ((UserInfo) authentication.getPrincipal());
        logger.info("Checking token permissions in auth: " + userInfo.toString());

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    logger.info("Id: " + id);
                    CourseExecutionDto courseExecutionDto = authRequiredService.getCourseExecutionById(id);
                    return courseExecutionDto.getName().equals("Demo Course");
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
