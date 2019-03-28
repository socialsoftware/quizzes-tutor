package com.example.tutor.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tutor.ResourceNotFoundException;

import javax.validation.Valid;

@RestController
public class QuizController {

    private QuizRepository quizRepository;

    QuizController(QuizRepository repository) {
        this.quizRepository = repository;
    }

    @GetMapping("/quizs")
    public Page<Quiz> getQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    @GetMapping("/quizs/{quizID}")
    public Quiz getQuiz(@PathVariable Integer quizID) {
        return quizRepository.findById(quizID)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }


    @PostMapping("/quizs")
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @PutMapping("/quizs/{quizID}")
    public Quiz updateQuiz(@PathVariable Integer quizID,
                                   @Valid @RequestBody Quiz quizRequest) {
        return quizRepository.findById(quizID)
                .map(quiz -> {
                    quiz.setTitle(quizRequest.getTitle());
                    return quizRepository.save(quiz);
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }


    @DeleteMapping("/quizs/{quizID}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer quizID) {
        return quizRepository.findById(quizID)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }
}