package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_STUDENT" })
public class StatementController {
    private static Logger logger = LoggerFactory.getLogger(StatementController.class);

    @Autowired
    private StatementService statementService;

    @GetMapping("/executions/{executionId}/quizzes/available")
    public List<StatementQuizDto> getAvailableQuizzes(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getAvailableQuizzes(user.getUsername(), executionId);
    }

    @PostMapping("/executions/{executionId}/quizzes/generate")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.generateStudentQuiz(user.getUsername(), executionId, quizDetails);
    }

    @GetMapping("/executions/{executionId}/quizzes/solved")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getSolvedQuizzes(user.getUsername(), executionId);
    }

    @GetMapping("/quizzes/{quizId}/evaluation")
    public StatementQuizDto getEvaluationQuiz(Principal principal, @PathVariable int quizId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getEvaluationQuiz(user.getUsername(), quizId);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        statementService.submitAnswer(user.getUsername(), quizId, answer);
    }

    @GetMapping("/quizzes/{quizId}/conclude")
    public CorrectAnswersDto concludeQuiz(Principal principal, @PathVariable int quizId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.concludeQuiz(user.getUsername(), quizId);
    }


}