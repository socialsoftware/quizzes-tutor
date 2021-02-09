package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TopicController {
    private static Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private TopicService topicService;

    @GetMapping("/courses/{courseId}/topics")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<TopicDto> getCourseTopics(@PathVariable int courseId) {
        logger.debug("courseId {}", courseId);
        return this.topicService.findTopics(courseId);
    }

    @PostMapping(value = "/courses/{courseId}/topics")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public TopicDto createTopic(@PathVariable int courseId, @Valid @RequestBody TopicDto topicDto) {
        return this.topicService.createTopic(courseId, topicDto);
    }


    @PutMapping(value = "/topics/{topicId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#topicId, 'TOPIC.ACCESS')")
    public TopicDto updateTopic(@PathVariable Integer topicId, @Valid @RequestBody TopicDto topic) {
        return this.topicService.updateTopic(topicId, topic);
    }

    @DeleteMapping("/topics/{topicId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#topicId, 'TOPIC.ACCESS')")
    public void removeTopic(@PathVariable Integer topicId) {
        topicService.removeTopic(topicId);
    }

    @GetMapping("/topics/{topicId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#topicId, 'TOPIC.ACCESS')")
    public List<QuestionDto> getTopicQuestions(@PathVariable Integer topicId) {
        return this.topicService.getTopicQuestions(topicId);
    }
}