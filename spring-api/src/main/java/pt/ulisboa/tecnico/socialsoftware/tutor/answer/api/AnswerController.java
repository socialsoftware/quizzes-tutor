package pt.ulisboa.tecnico.socialsoftware.tutor.answer.api;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.service.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/quiz-answers")
    public CorrectAnswersDto correctAnswers(Principal principal, @Valid @RequestBody ResultAnswersDto answers) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return null;
        }

        return answerService.submitQuestionsAnswers(user, answers);
    }
}