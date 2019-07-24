package pt.ulisboa.tecnico.socialsoftware.tutor.topic.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.topic.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.topic.domain.Topic;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TopicController {

    private TopicRepository topicRepository;

    TopicController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @GetMapping("/topics")
    public List<Topic> getTopics(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return topicRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }

    @GetMapping("/topiczes/{topicId}")
    public Topic getTopic(@PathVariable Integer topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id " + topicId));
    }

    @PostMapping("/topics")
    public Topic createTopic(@Valid @RequestBody Topic topic) {
        return topicRepository.save(topic);
    }

    @PutMapping("/topics/{topicId}")
    public Topic updateTopic(@PathVariable Integer topicId,
                               @Valid @RequestBody Topic topicRequest) {
        return topicRepository.findById(topicId)
                .map(topic -> {
                    topic.setName(topicRequest.getName());
                    topic.setParent(topicRequest.getParent());
                    return topicRepository.save(topic);
                }).orElseThrow(() -> new ResourceNotFoundException("Topic not found with id " + topicId));
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable Integer topicId) {
        return topicRepository.findById(topicId)
                .map(topic -> {
                    topicRepository.delete(topic);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Topic not found with id " + topicId));
    }
}