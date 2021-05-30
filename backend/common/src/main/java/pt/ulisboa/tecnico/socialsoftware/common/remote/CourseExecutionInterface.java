package pt.ulisboa.tecnico.socialsoftware.common.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;

public class CourseExecutionInterface implements CourseExecutionContract {
    private static final Logger logger = LoggerFactory.getLogger(CourseExecutionInterface.class);

    private static final String ENDPOINT = "http://tutor-service:8084";

    @Override
    public CourseExecutionDto findCourseExecution(Integer courseExecutionId) {
        logger.info("findCourseExecution id:{}", courseExecutionId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            CourseExecutionDto courseExecutionDto = restTemplate.getForObject(ENDPOINT + "rest/execution/find?courseExecutionId=" + courseExecutionId,
                    CourseExecutionDto.class);
            logger.info("CourseExecutionDto: {}", courseExecutionDto);
            return courseExecutionDto;
        } catch (HttpClientErrorException e) {
            logger.info("findCourseExecution HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("findCourseExecution Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }

    @Override
    public CourseExecutionDto findCourseExecutionByFields(String acronym, String academicTerm, String type) {
        logger.info("findCourseExecutionByFields");
        RestTemplate restTemplate = new RestTemplate();
        try {
            CourseExecutionDto courseExecutionDto = restTemplate.getForObject(ENDPOINT + "rest/execution/findByFields?acronym="
                            + acronym + "&academicTerm=" + academicTerm + "&type=" + type,
                    CourseExecutionDto.class);
            logger.info("findCourseExecutionByFields: {}", courseExecutionDto);
            return courseExecutionDto;
        } catch (HttpClientErrorException e) {
            logger.info("findCourseExecutionByFields HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("findCourseExecutionByFields Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }

    @Override
    public Integer findDemoCourseExecution() {
        logger.info("findDemoCourseExecution");
        RestTemplate restTemplate = new RestTemplate();
        try {
            Integer demoCourseExecutionId = restTemplate.getForObject(ENDPOINT + "rest/execution/demo",
                    Integer.class);
            logger.info("findDemoCourseExecution: {}", demoCourseExecutionId);
            return demoCourseExecutionId;
        } catch (HttpClientErrorException e) {
            logger.info("findDemoCourseExecution HttpClientErrorException errorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("findDemoCourseExecution Exception errorMessage:{}", e.getMessage());
            throw new RemoteAccessException();
        }
    }
}
