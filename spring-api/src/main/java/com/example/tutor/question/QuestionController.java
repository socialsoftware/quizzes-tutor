package com.example.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tutor.ResourceNotFoundException;

import javax.validation.Valid;

@RestController
public class QuestionController {

    private QuestionRepository questionRepository;

    QuestionController(QuestionRepository repository) {
        this.questionRepository = repository;
    }

    @GetMapping("/questions")
    public Page<Question> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @GetMapping("/questions/{questionID}")
    public Question getQuestion(@PathVariable Integer questionID) {
        return questionRepository.findById(questionID)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionID));
    }


    @PostMapping("/questions")
    public Question createQuestion(@Valid @RequestBody Question question) {
        return questionRepository.save(question);
    }

    @PutMapping("/questions/{questionID}")
    public Question updateQuestion(@PathVariable Integer questionID,
                                   @Valid @RequestBody Question questionRequest) {
        return questionRepository.findById(questionID)
                .map(question -> {
                    question.setContent(questionRequest.getContent());
                    return questionRepository.save(question);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionID));
    }


    @DeleteMapping("/questions/{questionID}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionID) {
        return questionRepository.findById(questionID)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionID));
    }
}