package pt.ulisboa.tecnico.socialsoftware.common.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;

public class AnswerInterface implements AnswerContract {
    private static final Logger logger = LoggerFactory.getLogger(AnswerInterface.class);

    private static final String ENDPOINT = "http://tutor-service:8084";

    @Override
    public StatementQuizDto startQuiz(Integer userId, Integer quizId) {
        logger.info("startQuiz userId:{} quizId:{}", userId, quizId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            StatementQuizDto statementQuizDto = restTemplate.getForObject(ENDPOINT + "/rest/answers/startQuiz?userId="
                            + userId + "&quizId=" + quizId,
                    StatementQuizDto.class);
            logger.info("startQuiz: {}", statementQuizDto);
            return statementQuizDto;
        } catch (HttpClientErrorException e) {
            logger.info("startQuiz HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("startQuiz Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }

    @Override
    public StatementQuizDto getStatementQuiz(Integer userId, Integer quizId) {
        logger.info("getStatementQuiz userId:{} quizId:{}", userId, quizId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            StatementQuizDto statementQuizDto = restTemplate.getForObject(ENDPOINT + "/rest/answers/getStatementQuiz?userId="
                            + userId + "&quizId=" + quizId,
                    StatementQuizDto.class);
            logger.info("getStatementQuiz: {}", statementQuizDto);
            return statementQuizDto;
        } catch (HttpClientErrorException e) {
            logger.info("getStatementQuiz HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("getStatementQuiz Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }
}
