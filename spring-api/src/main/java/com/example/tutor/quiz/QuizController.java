package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.quiz.Quiz;
import com.example.tutor.quiz.QuizDTO;
import com.example.tutor.quiz.QuizRepository;
import com.example.tutor.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;


    @GetMapping("/quizzes")
    public List<QuizDTO> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizService.findAll(pageIndex, pageSize).stream().map(QuizDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDTO getQuiz(@PathVariable Integer quizId) {
        return new QuizDTO(this.quizService.findById(quizId));
    }


    @PostMapping("/quizzes")
    public ResponseEntity createQuiz(@Valid @RequestBody QuizDTO quiz) {
        this.quizService.create(quiz);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/quizzes/{quizId}")
    public ResponseEntity updateQuiz(@PathVariable Integer quizId,
                                         @Valid @RequestBody QuizDTO quizRequest) {

        quizService.update(quizId, quizRequest);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity deleteQuiz(@PathVariable Integer quizId) {
        quizService.delete(quizId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/quizzes/generate")
    public StudentQuizDTO getNewQuiz(Principal principal, @RequestBody QuizSetupDTO quizDetails) {

        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = new Quiz();

        quiz.generate(questionRepository, user.getId(), quizDetails.getNumberOfQuestions(), quizDetails.getTopics(), quizDetails.getQuestionType());

        quiz.getQuizQuestion().forEach(question ->
                quizHasQuestionRepository.save(question));

        quizRepository.save(quiz);

        return new StudentQuizDTO(quiz);
    }


}