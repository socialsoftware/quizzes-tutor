package pt.ulisboa.tecnico.socialsoftware.tutor.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN" })
public class AccessController {

    @Autowired
    private AccessRepository accessRepository;

    @GetMapping("/accesses")
    public List<Access> getAccess(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return accessRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }
}