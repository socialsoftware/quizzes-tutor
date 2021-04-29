package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @GetMapping("/executions/{executionId}/quizzes/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuizDto> getAvailableQuizzes(Principal principal, @PathVariable int executionId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getAvailableQuizzes(userInfo.getId(), executionId);
    }

    @PostMapping("/executions/{executionId}/quizzes/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.generateStudentQuiz(userInfo.getId(), executionId, quizDetails);
    }

    @GetMapping("/executions/{executionId}/quizzes/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getSolvedQuizzes(userInfo.getId(), executionId);
    }

    @GetMapping("/quizzes/{quizId}/byqrcode")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto getQuizByQRCode(Principal principal, @PathVariable int quizId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getQuizByQRCode(userInfo.getId(), quizId);
    }

    @GetMapping("/quizzes/{quizId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto startQuiz(Principal principal, @PathVariable int quizId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.startQuiz(userInfo.getId(), quizId);
    }

    @GetMapping("/quizzes/{quizId}/question/{questionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuestionDto getQuestionForQuizAnswer(@PathVariable int quizId, @PathVariable int questionId) {
        return answerService.getQuestionForQuizAnswer(quizId, questionId);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        answerService.submitAnswer(userInfo.getUsername(), quizId, answer);
    }

    @PostMapping("/quizzes/{quizId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<CorrectAnswerDto> concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        return answerService.concludeQuiz(statementQuizDto);
    }

}