package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class TopicController {
    private static Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private TopicService topicService;

    @GetMapping("/topics")
    public List<TopicDto> getTopics() {
        return this.topicService.findAllTopics();
    }

    @PostMapping(value = "/topics")
    public TopicDto createTopic(@Valid @RequestBody TopicDto topicDto) {
        return this.topicService.createTopic(topicDto);
    }

    @PutMapping(value = "/topics/{topicId}")
    public TopicDto updateTopic(@PathVariable Integer topicId, @Valid @RequestBody TopicDto topic) {
        return this.topicService.updateTopic(topicId, topic);
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity removeTopic(@PathVariable Integer topicId) {
        topicService.removeTopic(topicId);

        return ResponseEntity.ok().build();
    }

}