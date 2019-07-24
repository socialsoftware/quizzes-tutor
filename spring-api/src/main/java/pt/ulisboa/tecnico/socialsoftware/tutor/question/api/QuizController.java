package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuizSetupDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.StudentQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @Transactional
    @PostMapping("/quizzes/generate/student")
    public StudentQuizDto getNewQuiz(Principal principal, @RequestBody QuizSetupDto quizDetails) {

        // TODO: Check how is this object materialized
        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizService.generateStudentQuiz(user.getId(), quizDetails.getNumberOfQuestions());

        // TODO: It is necessary to consider the creation of a quiz answer associated to the student, eventually inside de service

        return new StudentQuizDto(quiz);
    }


}