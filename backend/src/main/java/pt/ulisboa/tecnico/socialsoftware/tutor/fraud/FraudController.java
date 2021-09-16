package pt.ulisboa.tecnico.socialsoftware.tutor.fraud;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudGraphScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto.QuizFraudScoreDto;

@RestController
public class FraudController {

    @Value("${fraud.service.url}")
    private String fraudServiceURL;

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
        return getFraudScoresFromURI("/quiz/" + quizId.toString());
    }

    @GetMapping("/fraud/graph/quiz/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizFraudGraphScoreDto getQuizGraphFraudScores(@PathVariable Integer quizId) {
        return getFraudGraphScoresFromURI("/graph/quiz/" + quizId.toString());
    }

    private List<QuizFraudScoreDto> getFraudScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<QuizFraudScoreDto>>() {
                }).log().block();
    }
    
    private QuizFraudGraphScoreDto getFraudGraphScoresFromURI(String uri) {
        WebClient client = WebClient.create(fraudServiceURL);
        return client.get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(new ParameterizedTypeReference<QuizFraudGraphScoreDto>() {
                }).log().block();
    }
}
