package pt.ulisboa.tecnico.socialsoftware.tutor.fraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudCommunicationScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudTimeScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

// import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_CONCLUDED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_HAS_NO_ANSWERS;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FRAUD_SERVICE_QUIZ_TYPE_NOT_SUPPORTED;

@RestController
public class FraudController {

    @Value("${fraud.service.url}")
    private String fraudServiceURL;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/fraud/execution/{executionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'EXECUTION.ACCESS')")
    public QuizFraudTimeScoreDto getCourseExecutionFraudScores(@PathVariable Integer executionId) {
        return getFraudTimeScoresFromURI("/execution/" + executionId.toString());
    }

    @GetMapping("/fraud/execution/{executionId}/user/{userId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'EXECUTION.ACCESS')")
    public QuizFraudTimeScoreDto getUserFraudScores(@PathVariable Integer executionId, @PathVariable Integer userId) {
        return getFraudTimeScoresFromURI("/execution/" + executionId.toString() + "/user/" + userId.toString());
    }

    @GetMapping("/fraud/time/quiz/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizFraudTimeScoreDto getQuizTimeFraudScores(@PathVariable Integer quizId) {
        answerService.writeQuizAnswers(quizId);
        validateQuizId(quizId);
        return getFraudTimeScoresFromURI("/time/quiz/" + quizId.toString());
    }

    @GetMapping("/fraud/communication/quiz/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizFraudCommunicationScoreDto getQuizCommunicationFraudScores(@PathVariable Integer quizId) {
        validateQuizId(quizId);
        return getFraudCommunicationScoresFromURI("/communication/quiz/" + quizId.toString());
    }

    private QuizFraudTimeScoreDto getFraudTimeScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<QuizFraudTimeScoreDto>() {
                }).block();
    }

    private QuizFraudCommunicationScoreDto getFraudCommunicationScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<QuizFraudCommunicationScoreDto>() {
                }).block();
    }

    private void validateQuizId(Integer quizId) {
        QuizDto quiz = quizService.findById(quizId);
        if (!quiz.isOneWay() || !quiz.getType().equals(Quiz.QuizType.IN_CLASS.toString())) {
            throw new TutorException(FRAUD_SERVICE_QUIZ_TYPE_NOT_SUPPORTED);

            // } else if
            // (ZonedDateTime.parse(quiz.getConclusionDate()).isAfter(ZonedDateTime.now()))
            // {
            // throw new TutorException(QUIZ_NOT_YET_CONCLUDED);

        } else if (quiz.getNumberOfAnswers() == 0) {
            throw new TutorException(QUIZ_HAS_NO_ANSWERS);
        }
    }
}
