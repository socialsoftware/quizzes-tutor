package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionDTO;
import com.example.tutor.question.QuestionRepository;
import com.example.tutor.user.User;
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

    private QuizService quizService;
    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private QuizHasQuestionRepository quizHasQuestionRepository;

    QuizController(QuizRepository quizRepository, QuestionRepository questionRepository, QuizHasQuestionRepository quizHasQuestionRepository, QuizService quizService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizHasQuestionRepository = quizHasQuestionRepository;
        this.quizService = quizService;
    }

    @GetMapping("/quizzes")
    public List<QuizDTO> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuizDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDTO getQuiz(@PathVariable Integer quizId) {
        return new QuizDTO(this.quizService.findById(quizId));
        /*return new QuizDTO(quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId)));*/
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
                    quiz.setGeneratedBy(quizRequest.getGeneratedBy());
                    quiz.setQuestions(quizRequest.getQuestions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Question(e.getValue()))));
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

    @Transactional
    @PostMapping("/newquiz")
    public StudentQuizDTO getNewQuiz(Principal principal, @RequestBody QuizSetupDTO quizDetails) {

        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = new Quiz();

        quiz.generate(questionRepository, user.getId(), quizDetails.getNumberOfQuestions(), quizDetails.getTopics(), quizDetails.getQuestionType());

        quiz.getQuizHasQuestion().forEach(question ->
                quizHasQuestionRepository.save(question));

        quizRepository.save(quiz);

        return new StudentQuizDTO(quiz);
    }
}