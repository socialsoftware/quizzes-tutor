package pt.ulisboa.tecnico.socialsoftware.tutor.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN" })
public class LogController {

    @Autowired
    private LogRepository logRepository;

    @GetMapping("/logs")
    public List<Log> getLog(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return logRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }
}