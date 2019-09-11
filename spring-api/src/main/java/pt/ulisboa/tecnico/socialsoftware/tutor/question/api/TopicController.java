package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TopicController {
    private static Logger logger = LoggerFactory.getLogger(TopicController.class);

    private QuestionService questionService;

    TopicController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/topics")
    public List<String> getTopics() {
        return this.questionService.findAllTopics();
    }

    @PostMapping(value = "/topics", consumes = "text/plain")
    public ResponseEntity createTopic(@Valid @RequestBody String topic) {
        logger.debug("createTopic topic: {}", topic);

        this.questionService.createTopic(topic);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/topics/{topic}", consumes = "text/plain")
    public ResponseEntity updateTopic(@PathVariable String topic, @Valid @RequestBody String newName) {
        logger.debug("updateTopic oldTopic: {}, newTopic: {}", topic, newName);

        this.questionService.updateTopic(topic, newName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/topics/{topic}")
    public ResponseEntity removeTopic(@PathVariable String topic) {
        logger.debug("removeTopic topic: {}: ", topic);

        questionService.removeTopic(topic);

        return ResponseEntity.ok().build();
    }

}