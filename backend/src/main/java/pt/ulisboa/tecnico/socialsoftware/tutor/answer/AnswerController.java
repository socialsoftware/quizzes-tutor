package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_FOUND;

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

        return answerService.getAvailableQuizzes(authUser.getUser().getId(), executionId);
    }

    @PostMapping("/answers/{executionId}/quizzes/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.generateStudentQuiz(authUser.getUser().getId(), executionId, quizDetails);
    }

    @GetMapping("/answers/{executionId}/quizzes/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getSolvedQuizzes(authUser.getUser().getId(), executionId);
    }

    @GetMapping("/answers/{quizId}/byqrcode")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto getQuizByQRCode(Principal principal, @PathVariable int quizId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getQuizByQRCode(authUser.getUser().getId(), quizId);
    }

    @GetMapping("/answers/{executionId}/bycode/{code}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getQuizByCode(Principal principal, @PathVariable int executionId, @PathVariable int code) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizRepository.findByCourseExecutionAndCode(executionId, code).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, code));

        return answerService.getQuizByQRCode(authUser.getUser().getId(), quiz.getId());
    }

    @GetMapping("/answers/{quizId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto startQuiz(Principal principal, @PathVariable int quizId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.startQuiz(authUser.getUser().getId(), quizId);
    }

    @PostMapping("/answers/{quizId}/question/{questionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuestionDto getQuestionForQuizAnswer(Principal principal, @PathVariable int quizId, @PathVariable int questionId, @Valid @RequestBody StatementAnswerDto answer) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return answerService.getQuestionForQuizAnswer(authUser.getUser().getUsername(), quizId, questionId, answer);
    }

    @PostMapping("/answers/{quizId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        answerService.submitAnswer(authUser.getUser().getUsername(), quizId, answer);
    }

    @PostMapping("/answers/{quizId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<CorrectAnswerDto> concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        return answerService.concludeQuiz(statementQuizDto);
    }

}