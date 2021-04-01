package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class QuizController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/executions/{executionId}/quizzes/non-generated")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuizDto> findNonGeneratedQuizzes(@PathVariable int executionId) {
        return quizService.findNonGeneratedQuizzes(executionId);
    }

    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PostMapping("/executions/{executionId}/quizzes")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuizDto createQuiz(@PathVariable int executionId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.createQuiz(executionId, quiz);
    }

    @PutMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto updateQuiz(@PathVariable Integer quizId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.updateQuiz(quizId, quiz);
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void deleteQuiz(@PathVariable Integer quizId) {
        quizService.removeQuiz(quizId);
    }

    @GetMapping(value = "/quizzes/{quizId}/export")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void exportQuiz(HttpServletResponse response, @PathVariable Integer quizId) throws IOException {
        answerService.writeQuizAnswers(quizId);

        response.setHeader("Content-Disposition", "attachment; filename=file.zip");
        response.setContentType("application/zip");
        response.getOutputStream().write(this.quizService.exportQuiz(quizId).toByteArray());

        response.flushBuffer();
    }

    @PostMapping("/quizzes/{quizId}/populate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto populateWithQuizAnswers(@PathVariable Integer quizId) {
        return this.quizService.populateWithQuizAnswers(quizId);
    }

    @PostMapping("/quizzes/{quizId}/unpopulate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto removeNonFilledQuizAnswers(@PathVariable Integer quizId) {
        return this.quizService.removeNonFilledQuizAnswers(quizId);
    }

    @GetMapping("/quizzes/{quizId}/answers")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizAnswersDto getQuizAnswers(@PathVariable Integer quizId) {
        answerService.writeQuizAnswers(quizId);
        return this.quizService.getQuizAnswers(quizId);
    }
}