package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class QuestionController {

    private QuestionService questionService;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<QuestionDto> getQuestions(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return this.questionService.findAll(pageIndex, pageSize).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @GetMapping("/questions/{questionId}")
    public QuestionDto getQuestion(@PathVariable Integer questionId) {
        return new QuestionDto(this.questionService.findById(questionId));
    }

    @PostMapping("/questions")
    public ResponseEntity createQuestion(@Valid @RequestBody QuestionDto question) {
        this.questionService.create(question);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable Integer questionId,
                                   @Valid @RequestBody QuestionDto questionRequest) {

        questionService.update(questionId, questionRequest);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity deleteQuestion(@PathVariable Integer questionId) {
        questionService.delete(questionId);
        return ResponseEntity.ok().build();
    }
}