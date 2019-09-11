package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class QuizController {
    private static Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizService quizService;

    @GetMapping("/quizzes")
    public List<QuizDto> getQuizzes(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return quizService.findAll(pageIndex, pageSize).stream().map(quiz -> new QuizDto(quiz, true)).collect(Collectors.toList());
    }

    @GetMapping("/quizzes/nongenerated")
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