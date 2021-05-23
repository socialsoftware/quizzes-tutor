package pt.ulisboa.tecnico.socialsoftware.common.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;

public class UserInterface implements UserContract {
    private static final Logger logger = LoggerFactory.getLogger(UserInterface.class);

    private static final String ENDPOINT = "http://tutor-service:8084";

    @Override
    public UserDto findUser(Integer userId) {
        logger.info("findUser id:{}", userId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            UserDto userDto = restTemplate.getForObject(ENDPOINT + "rest/users/find?userId=" + userId,
                    UserDto.class);
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

    @Override
    public UserCourseExecutionsDto getUserCourseExecutions(Integer userId) {
        logger.info("getUserCourseExecutions id:{}", userId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            UserCourseExecutionsDto userCourseExecutions = restTemplate.getForObject(ENDPOINT + "rest/users/executions?userId=" + userId,
                    UserCourseExecutionsDto.class);
            logger.info("CourseExecutionDtoList: {}", userCourseExecutions);
            return userCourseExecutions;
        } catch (HttpClientErrorException e) {
            logger.info(
                    "getUserCourseExecutions HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("getUserCourseExecutions Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }
}
