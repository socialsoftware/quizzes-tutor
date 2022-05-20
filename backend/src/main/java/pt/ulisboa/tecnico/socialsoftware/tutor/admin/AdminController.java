package pt.ulisboa.tecnico.socialsoftware.tutor.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/anonymize/users/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void anonymizeUser(@PathVariable String username) {
        adminService.anonymizeUser(username);
    }

    @GetMapping("/admin/anonymize/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void anonymizeCourseExecutionUsers(@PathVariable int executionId) {
        adminService.anonymizeCourseExecutionUsers(executionId);
    }
}
