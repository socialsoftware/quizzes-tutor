package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserService userService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String username = ((User) authentication.getPrincipal()).getUsername();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "CREATE":
                    return userService.getCourseExecutionIds(username).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                default:
                    assert false;
            }
        }

        if (targetDomainObject instanceof String) {
            String acronym = (String) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "ACCESS":
                    return userService.getCourseExecutions(username).stream()
                            .anyMatch(course -> course.getAcronym().equals(acronym));
                default:
                    assert false;
            }
        }


        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
