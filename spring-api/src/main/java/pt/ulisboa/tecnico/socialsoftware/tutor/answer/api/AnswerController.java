package pt.ulisboa.tecnico.socialsoftware.tutor.answer.api;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Answer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.service.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.WrongParametersException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/quiz-answers")
    public CorrectAnswersDto correctAnswers(Principal principal, @Valid @RequestBody ResultAnswersDto answers) {

        User user = (User) ((Authentication) principal).getPrincipal();

        return answerService.correctAnswers(user, answers);

    }
}