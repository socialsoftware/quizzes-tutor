package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/quizzes")
    public List<QuizDto> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizService.findAll(pageIndex, pageSize);
    }

    @GetMapping("/quizzes/non-generated")
    public List<QuizDto> getNonGeneratedQuizzes() {
        return quizService.findAllNonGenerated();
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PostMapping("/quizzes")
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quiz) {
        return this.quizService.createQuiz(quiz);
    }

    @PutMapping("/quizzes/{quizId}")
    public QuizDto updateQuiz(@PathVariable Integer quizId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.updateQuiz(quizId, quiz);
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity deleteQuiz(@PathVariable Integer quizId) {
        quizService.removeQuiz(quizId);

        return ResponseEntity.ok().build();
    }
}