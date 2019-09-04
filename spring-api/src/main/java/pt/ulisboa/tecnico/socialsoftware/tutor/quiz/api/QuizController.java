package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.QuestionController;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizSetupDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizStatementDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.nio.file.Files;
import java.security.Principal;
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
        logger.debug("getNonGeneratedQuizzes");
        return quizService.findAllNonGenerated();
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PostMapping("/quizzes")
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quiz) {
        return new QuizDto(this.quizService.createQuiz(quiz),true);
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity deleteQuiz(@PathVariable Integer quizId) {
        logger.debug("deleteQuiz quizId: {}: ", quizId);

        quizService.removeQuiz(quizId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/quizzes/generate/student")
    public QuizStatementDto getNewQuiz(Principal principal, @RequestBody QuizSetupDto quizDetails) {

        User user = (User) ((Authentication) principal).getPrincipal();

        return quizService.generateStudentQuiz(user, quizDetails.getNumberOfQuestions());
    }


}