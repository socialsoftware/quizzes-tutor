package com.example.tutor.question;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.image.Image;
import com.example.tutor.option.Option;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<QuestionDTO> getQuestions(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return this.questionService.findAll(pageIndex, pageSize).stream().map(QuestionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/questions/{questionId}")
    public QuestionDTO getQuestion(@PathVariable Integer questionId) {
        return new QuestionDTO(this.questionService.findById(questionId));
    }

    @PostMapping("/questions")
    public ResponseEntity createQuestion(@Valid @RequestBody QuestionDTO question) {
        this.questionService.create(question);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable Integer questionId,
                                   @Valid @RequestBody QuestionDTO questionRequest) {

        questionService.update(questionId, questionRequest);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity deleteQuestion(@PathVariable Integer questionId) {
        questionService.delete(questionId);
        return ResponseEntity.ok().build();
    }
}