package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;
import com.example.tutor.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class QuizController {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;

    QuizController(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping("/quizzes")
    public List<QuizDTO> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuizDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDTO getQuiz(@PathVariable Integer quizId) {
        return new QuizDTO(quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId)));
    }

    @PostMapping("/quizzes")
    public QuizDTO createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        quizRepository.save(new Quiz(quizDTO));
        return quizDTO;
    }

    @PutMapping("/quizzes/{quizId}")
    public QuizDTO updateQuiz(@PathVariable Integer quizId,
                                   @Valid @RequestBody QuizDTO quizRequest) {
        quizRepository.findById(quizId)
                .map(quiz -> {
                    quiz.setTitle(quizRequest.getTitle());
                    quiz.setCompleted(quizRequest.getCompleted());
                    quiz.setDate(quizRequest.getDate());
                    quiz.setGenerated_by(quizRequest.getGenerated_by());
                    quiz.setQuestions(quizRequest.getQuestions().stream().map(Question::new).collect(Collectors.toList()));
                    quiz.setYear(quizRequest.getYear());
                    quiz.setVersion(quizRequest.getVersion());
                    quiz.setType(quizRequest.getType());
                    quiz.setSeries(quizRequest.getSeries());
                    return quizRepository.save(quiz);
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId));
        return quizRequest;
    }


    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer quizId) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId));
    }

    @PostMapping("/newquiz")
    public StudentQuizDTO getNewQuiz(Principal principal, @RequestBody QuizSetupDTO quizDetails) {

        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = new Quiz();

        quiz.generate(questionRepository, user.getId(), quizDetails.getNumberOfQuestions(), quizDetails.getTopics(), quizDetails.getQuestionType());

        return new StudentQuizDTO(quizRepository.save(quiz));
    }
}