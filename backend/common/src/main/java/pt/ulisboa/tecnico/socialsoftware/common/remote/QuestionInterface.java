package pt.ulisboa.tecnico.socialsoftware.common.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;

public class QuestionInterface implements QuestionContract {
    private static final Logger logger = LoggerFactory.getLogger(QuestionInterface.class);

    private static final String ENDPOINT = "http://localhost:8080";

    @Override
    public FindTopicsDto findTopics(TopicListDto topicsList) {
        logger.info("findTopics topicsList:{}", topicsList);
        RestTemplate restTemplate = new RestTemplate();
        try {
            FindTopicsDto findTopicsDto = restTemplate.postForObject(ENDPOINT + "/rest/questions/findTopics",
                    topicsList, FindTopicsDto.class);
            logger.info("findTopics: {}", findTopicsDto);
            return findTopicsDto;
        } catch (HttpClientErrorException e) {
            logger.info("findTopics HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("findTopics Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }
}
