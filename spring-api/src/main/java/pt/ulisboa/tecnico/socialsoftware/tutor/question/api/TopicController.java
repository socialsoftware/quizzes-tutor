package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

//    @PutMapping("/questions/{questionId}")
//    public ResponseEntity updateQuestion(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
//        logger.debug("updateQuestion questionId: {}, title: {}, content: {}, options: {}: ", questionId,
//                question.getTitle(), question.getContent(),
//                question.getOptions().stream().map(optionDto -> optionDto.getId() + " : " + optionDto.getContent() + " : " + optionDto.getCorrect())
//                        .collect(Collectors.joining("\n")));
//        this.questionService.updateQuestion(questionId, question);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/questions/{questionId}")
//    public ResponseEntity removeQuestion(@PathVariable Integer questionId) throws IOException {
//        logger.debug("removeQuestion questionId: {}: ", questionId);
//        QuestionDto questionDto = questionService.findQuestionById(questionId);
//        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;
//
//        questionService.removeQuestion(questionId);
//
//        if (url != null && Files.exists(getTargetLocation(url))) {
//            Files.delete(getTargetLocation(url));
//        }
//
//        return ResponseEntity.ok().build();
//    }

}