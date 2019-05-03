package com.example.tutor.quiz;

import com.example.tutor.question.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tutor.ResourceNotFoundException;

import javax.validation.Valid;

@RestController
public class QuizController {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;

    QuizController(QuizRepository repository, QuestionRepository rep2) {
        this.quizRepository = repository;
        this.questionRepository = rep2;
    }

    @GetMapping("/quizzes")
    public Page<Quiz> getQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    @GetMapping("/quizzes/{quizID}")
    public Quiz getQuiz(@PathVariable Integer quizID) {
        return quizRepository.findById(quizID)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }


    @PostMapping("/quizzes")
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @PutMapping("/quizzes/{quizID}")
    public Quiz updateQuiz(@PathVariable Integer quizID,
                                   @Valid @RequestBody Quiz quizRequest) {
        return quizRepository.findById(quizID)
                .map(quiz -> {
                    quiz.setTitle(quizRequest.getTitle());
                    return quizRepository.save(quiz);
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }


    @DeleteMapping("/quizzes/{quizID}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer quizID) {
        return quizRepository.findById(quizID)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizID));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/newquiz")
    public Quiz getNewQuiz() {
        return quizRepository.save(new Quiz(questionRepository));
    }
}