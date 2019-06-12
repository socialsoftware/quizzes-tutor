package com.example.tutor.quiz;

import com.example.tutor.auth.JwtTokenProvider;
import com.example.tutor.question.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tutor.ResourceNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class QuizController {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private JwtTokenProvider tokenProvider;

    QuizController(QuizRepository repository, QuestionRepository rep2, JwtTokenProvider tokenProvider) {
        this.quizRepository = repository;
        this.questionRepository = rep2;
        this.tokenProvider = tokenProvider;

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

    @GetMapping("/newquiz")
    public QuizDTO getNewQuiz(@RequestParam("token") String token, @RequestParam("numberOfQuestions") Integer numberOfQuestions, @RequestParam("questions") String questions, @RequestParam("topic") String topic) {
        if (tokenProvider.verifyToken(token)) {
            Integer userId = tokenProvider.getUserIdFromJWT(token);
            Quiz quiz = new Quiz(questionRepository, userId, numberOfQuestions, topic, questions);
            return new QuizDTO(quizRepository.save(quiz));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Token not valid");
        }
    }
}