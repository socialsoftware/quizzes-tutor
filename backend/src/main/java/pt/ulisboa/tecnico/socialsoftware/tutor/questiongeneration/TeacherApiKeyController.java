package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.TeacherApiKeyRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.TeacherApiKeysDto;

/** A teacher's own API keys for question generation. Each teacher only ever sees and changes their own. */
@RestController
public class TeacherApiKeyController {
    @Autowired
    private TeacherApiKeyService teacherApiKeyService;

    @GetMapping("/teacher/api-keys")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public TeacherApiKeysDto getKeys(Authentication authentication) {
        return teacherApiKeyService.getKeys(userId(authentication));
    }

    @PutMapping("/teacher/api-keys/{provider}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public TeacherApiKeysDto saveKey(Authentication authentication, @PathVariable String provider,
                                     @RequestBody TeacherApiKeyRequestDto request) {
        return teacherApiKeyService.saveKey(userId(authentication), provider, request.getKey());
    }

    @DeleteMapping("/teacher/api-keys/{provider}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public TeacherApiKeysDto removeKey(Authentication authentication, @PathVariable String provider) {
        return teacherApiKeyService.removeKey(userId(authentication), provider);
    }

    private Integer userId(Authentication authentication) {
        return ((AuthUser) authentication.getPrincipal()).getUser().getId();
    }
}
