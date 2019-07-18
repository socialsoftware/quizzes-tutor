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

    private QuestionRepository questionRepository;

    QuestionController(QuestionRepository repository) {
        this.questionRepository = repository;
    }

    @GetMapping("/questions")
    public List<QuestionDTO> getQuestions(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return questionRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuestionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/questions/{questionId}")
    public QuestionDTO getQuestion(@PathVariable Integer questionId) {
        return new QuestionDTO(questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId)));
    }

    @PostMapping("/questions")
    public QuestionDTO createQuestion(@Valid @RequestBody QuestionDTO question) {
        questionRepository.save(new Question(question));
        return question;
    }

    @PutMapping("/questions/{questionId}")
    public QuestionDTO updateQuestion(@PathVariable Integer questionId,
                                   @Valid @RequestBody QuestionDTO questionRequest) {

        questionRepository.findById(questionId)
                .map(question -> {
                    question.setContent(questionRequest.getContent());
                    question.setDifficulty(questionRequest.getDifficulty());
                    question.setImage(new Image(questionRequest.getImage()));
                    question.setActive(questionRequest.getActive());
                    question.setOptions(questionRequest.getOptions().stream().map(Option::new).collect(Collectors.toList()));
                    return questionRepository.save(question);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        return questionRequest;
    }


    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }
}