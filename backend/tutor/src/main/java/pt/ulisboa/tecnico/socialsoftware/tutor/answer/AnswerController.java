package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;
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
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getAvailableQuizzes(userInfo.getId(), executionId);
    }

    @PostMapping("/answers/{executionId}/quizzes/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.generateStudentQuiz(userInfo.getId(), executionId, quizDetails);
    }

    @GetMapping("/answers/{executionId}/quizzes/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getSolvedQuizzes(userInfo.getId(), executionId);
    }

    @GetMapping("/answers/{quizId}/byqrcode")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto getQuizByQRCode(Principal principal, @PathVariable int quizId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.getQuizByQRCode(userInfo.getId(), quizId);
    }

    @GetMapping("/answers/{executionId}/bycode/{code}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getQuizByCode(Principal principal, @PathVariable int executionId, @PathVariable int code) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizRepository.findByCourseExecutionAndCode(executionId, code).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, code));

        return answerService.getQuizByQRCode(userInfo.getId(), quiz.getId());
    }

    @GetMapping("/answers/{quizId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto startQuiz(Principal principal, @PathVariable int quizId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return answerService.startQuiz(userInfo.getId(), quizId);
    }

    @GetMapping("/answers/{quizId}/question/{questionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuestionDto getQuestionForQuizAnswer(@PathVariable int quizId, @PathVariable int questionId) {
        return answerService.getQuestionForQuizAnswer(quizId, questionId);
    }

    @PostMapping("/answers/{quizId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        answerService.submitAnswer(userInfo.getUsername(), quizId, answer);
    }

    @PostMapping("/answers/{quizId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<CorrectAnswerDto> concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        return answerService.concludeQuiz(statementQuizDto);
    }

}