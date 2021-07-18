package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;

@RestController
@RequestMapping(value = "/rest/questions")
public class QuestionInternalController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionInternalController.class);

    @Autowired
    MonolithService monolithService;

    @RequestMapping(value = "/findTopics", method = RequestMethod.GET)
    public ResponseEntity<FindTopicsDto> findTopics(@RequestBody TopicListDto topicListDto) {
        logger.info("findTopics topicListDto:{}", topicListDto);
        try {
            FindTopicsDto result = monolithService.findTopics(topicListDto);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
