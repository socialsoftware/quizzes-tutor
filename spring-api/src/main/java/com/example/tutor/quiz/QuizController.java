package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.QuestionRepository;
import com.example.tutor.user.User;
import com.example.tutor.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class QuizController {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private UserRepository userRepository;

    QuizController(QuizRepository quizRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;

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

    @PostMapping("/newquiz")
    public QuizDTO getNewQuiz(Principal principal, @RequestBody QuizDetailsDTO quizDetails) {

        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = new Quiz(questionRepository, user.getId(), quizDetails.getNumberOfQuestions(), quizDetails.getTopics(), quizDetails.getQuestionType());

        return new QuizDTO(quizRepository.save(quiz));
    }
}