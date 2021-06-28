package pt.ulisboa.tecnico.socialsoftware.apigateway.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.QUIZ_NOT_FOUND;

@RestController
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping("/answers/{executionId}/quizzes/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuizDto> getAvailableQuizzes(Principal principal, @PathVariable int executionId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getAvailableQuizzes(authUser.getUserSecurityInfo().getId(), executionId);
    }

    @PostMapping("/answers/{executionId}/quizzes/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.generateStudentQuiz(authUser.getUserSecurityInfo().getId(), executionId, quizDetails);
    }

    @GetMapping("/answers/{executionId}/quizzes/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getSolvedQuizzes(authUser.getUserSecurityInfo().getId(), executionId);
    }

    @GetMapping("/answers/{quizId}/byqrcode")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto getQuizByQRCode(Principal principal, @PathVariable int quizId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getQuizByQRCode(authUser.getUserSecurityInfo().getId(), quizId);
    }

    @GetMapping("/answers/{executionId}/bycode/{code}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getQuizByCode(Principal principal, @PathVariable int executionId, @PathVariable int code) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizRepository.findByCourseExecutionAndCode(executionId, code).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, code));

        return answerService.getQuizByQRCode(authUser.getUserSecurityInfo().getId(), quiz.getId());
    }

    @GetMapping("/answers/{quizId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto startQuiz(Principal principal, @PathVariable int quizId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.startQuiz(authUser.getUserSecurityInfo().getId(), quizId);
    }

    @GetMapping("/answers/{quizId}/question/{questionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuestionDto getQuestionForQuizAnswer(@PathVariable int quizId, @PathVariable int questionId) {
        return answerService.getQuestionForQuizAnswer(quizId, questionId);
    }

    @PostMapping("/answers/{quizId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        answerService.submitAnswer(authUser.getUsername(), quizId, answer);
    }

    @PostMapping("/answers/{quizId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<CorrectAnswerDto> concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        return answerService.concludeQuiz(statementQuizDto);
    }

}