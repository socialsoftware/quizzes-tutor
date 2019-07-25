package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizStatementDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizSetupDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;


    @GetMapping("/quizzes")
    public List<QuizDto> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizService.findAll(pageIndex, pageSize).stream().map(QuizDto::new).collect(Collectors.toList());
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return new QuizDto(this.quizService.findById(quizId));
    }


    @PostMapping("/quizzes")
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quiz) {
        return new QuizDto(this.quizService.create(quiz));
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity deleteQuiz(@PathVariable Integer quizId) {
        quizService.delete(quizId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/quizzes/generate/student")
    public QuizStatementDto getNewQuiz(Principal principal, @RequestBody QuizSetupDto quizDetails) {

        // TODO: Check how is this object materialized
        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizService.generateStudentQuiz(user.getId(), quizDetails.getNumberOfQuestions());

        // TODO: It is necessary to consider the creation of a quiz answer associated to the student, eventually inside de service

        return new QuizStatementDto(quiz);
    }


}