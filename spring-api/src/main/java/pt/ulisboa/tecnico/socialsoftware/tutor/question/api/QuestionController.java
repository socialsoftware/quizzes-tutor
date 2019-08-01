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
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private QuestionService questionService;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<QuestionDto> getQuestions(@RequestParam(value = "page", defaultValue = "-1") int pageIndex, @RequestParam(value = "size", defaultValue = "-1") int pageSize){
        if (pageIndex == -1 && pageSize == -1) {
            pageIndex = 0;
            pageSize = Integer.MAX_VALUE;
        }

        return this.questionService.findAll(pageIndex, pageSize);
    }

    @GetMapping("/questions/{questionId}")
    public QuestionDto getQuestion(@PathVariable Integer questionId) {
        return this.questionService.findById(questionId);
    }

    @PostMapping("/questions")
    public QuestionDto createQuestion(@Valid @RequestBody QuestionDto question) {
        logger.debug("createQuestion title: {}, content: {}, options: {}: ",
                question.getTitle(), question.getContent(),
                question.getOptions().stream().map(optionDto -> optionDto.getId() + " : " + optionDto.getContent() + " : " + optionDto.getCorrect())
                        .collect(Collectors.joining("\n")));
         return this.questionService.create(question);
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
        logger.debug("updateQuestion questionId: {}, title: {}, content: {}, options: {}: ", questionId,
                question.getTitle(), question.getContent(),
                question.getOptions().stream().map(optionDto -> optionDto.getId() + " : " + optionDto.getContent() + " : " + optionDto.getCorrect())
                        .collect(Collectors.joining("\n")));
        this.questionService.update(questionId, question);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity removeQuestion(@PathVariable Integer questionId) {
        logger.debug("removeQuestion questionId: {}: ", questionId);
        questionService.remove(questionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/questions/{questionId}/switchActive")
    public ResponseEntity questionSwitchActive(@PathVariable Integer questionId) {
        logger.debug("questionSwitchActive questionId: {}: ", questionId);
        questionService.questionSwitchActive(questionId);
        return ResponseEntity.ok().build();
    }

}