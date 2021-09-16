package pt.ulisboa.tecnico.socialsoftware.tutor.fraud;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudGraphScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_CONCLUDED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_HAS_NO_ANSWERS;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FRAUD_SERVICE_QUIZ_TYPE_NOT_SUPPORTED;

@RestController
public class FraudController {

    @Value("${fraud.service.url}")
    private String fraudServiceURL;

    @Autowired
    private QuizService quizService;

    @GetMapping("/fraud/execution/{executionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<QuizFraudScoreDto> getCourseExecutionFraudScores(@PathVariable Integer executionId) {
        return getFraudScoresFromURI("/execution/" + executionId.toString());
    }

    @GetMapping("/fraud/execution/{executionId}/user/{userId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<QuizFraudScoreDto> getUserFraudScores(@PathVariable Integer executionId, @PathVariable Integer userId) {
        return getFraudScoresFromURI("/execution/" + executionId.toString() + "/user/" + userId.toString());
    }

    @GetMapping("/fraud/quiz/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<QuizFraudScoreDto> getQuizFraudScores(@PathVariable Integer quizId) {
        validateQuizId(quizId);
        return getFraudScoresFromURI("/quiz/" + quizId.toString());
    }

    @GetMapping("/fraud/graph/quiz/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizFraudGraphScoreDto getQuizGraphFraudScores(@PathVariable Integer quizId) {
        validateQuizId(quizId);
        return getFraudGraphScoresFromURI("/graph/quiz/" + quizId.toString());
    }

    private List<QuizFraudScoreDto> getFraudScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<QuizFraudScoreDto>>() {
                }).block();
    }

    private QuizFraudGraphScoreDto getFraudGraphScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<QuizFraudGraphScoreDto>() {
                }).block();
    }

    private void validateQuizId(Integer quizId) {
        QuizDto quiz = quizService.findById(quizId);
        if (!quiz.isOneWay() || !quiz.isTimed() || !quiz.getType().equals(Quiz.QuizType.IN_CLASS.toString())) {
            throw new TutorException(FRAUD_SERVICE_QUIZ_TYPE_NOT_SUPPORTED);

        } else if (ZonedDateTime.parse(quiz.getConclusionDate()).isAfter(ZonedDateTime.now())) {
            throw new TutorException(QUIZ_NOT_YET_CONCLUDED);

        } else if (quiz.getNumberOfAnswers() == 0) {
            throw new TutorException(QUIZ_HAS_NO_ANSWERS);
        }
    }
}
