package pt.ulisboa.tecnico.socialsoftware.common.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;

public class UserInterface implements UserContract{
    private static final Logger logger = LoggerFactory.getLogger(UserInterface.class);

    private static final String ENDPOINT = "http://localhost:8080";

    @Override
    public UserDto findUser(Integer userId) {
        logger.info("findUser id:{}", userId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            UserDto userDto = restTemplate.postForObject(ENDPOINT + "rest/users/find?userId=" + userId,
                    null, UserDto.class);
            logger.info("UserDto: {}", userDto);
            return userDto;
        } catch (HttpClientErrorException e) {
            logger.info(
                    "findUser HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("findUser Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }
}
