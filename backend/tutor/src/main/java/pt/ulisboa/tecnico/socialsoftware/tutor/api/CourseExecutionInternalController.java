package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

@RestController
@RequestMapping(value = "/rest/execution")
public class CourseExecutionInternalController {
    private static final Logger logger = LoggerFactory.getLogger(CourseExecutionInternalController.class);

    @Autowired
    MonolithService monolithService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<CourseExecutionDto> findCourseExecution(@RequestParam Integer courseExecutionId) {
        logger.info("findCourseExecution id:{}", courseExecutionId);
        try {
            CourseExecutionDto result = monolithService.findCourseExecution(courseExecutionId);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public ResponseEntity<Integer> findDemoCourseExecution() {
        logger.info("findDemoCourseExecution");
        try {
            Integer result = monolithService.findDemoCourseExecution();
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findByFields", method = RequestMethod.GET)
    public ResponseEntity<CourseExecutionDto> findCourseExecutionByFields(@RequestParam String acronym,
                                                                          @RequestParam String academicTerm,
                                                                          @RequestParam String type) {
        logger.info("findCourseExecutionByFields");
        try {
            CourseExecutionDto result = monolithService.findCourseExecutionByFields(acronym, academicTerm, type);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
