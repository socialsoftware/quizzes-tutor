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
    public ResponseEntity createTopic(@Valid @RequestBody String topic) {
        logger.debug("createTopic topic: {}", topic);

        this.topicService.createTopic(topic);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/topics/{topic}")
    public ResponseEntity updateTopic(@PathVariable String topic, @Valid @RequestBody String newName) {
        logger.debug("updateTopic oldTopic: {}, newTopic: {}", topic, newName);

        this.topicService.updateTopic(topic, newName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/topics/{topic}")
    public ResponseEntity removeTopic(@PathVariable String topic) {
        logger.debug("removeTopic topic: {}: ", topic);

        topicService.removeTopic(topic);

        return ResponseEntity.ok().build();
    }

}