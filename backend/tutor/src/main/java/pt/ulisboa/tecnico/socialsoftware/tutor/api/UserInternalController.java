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
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

@RestController
@RequestMapping(value = "/rest/users")
public class UserInternalController {
    private static final Logger logger = LoggerFactory.getLogger(UserInternalController.class);

    @Autowired
    MonolithService monolithService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<UserDto> findUser(@RequestParam Integer userId) {
        logger.info("findUserById id:{}", userId);
        try {
            UserDto result = monolithService.findUser(userId);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/executions", method = RequestMethod.GET)
    public ResponseEntity<UserCourseExecutionsDto> findUserCourseExecutions(@RequestParam Integer userId) {
        logger.info("findUserCourseExecutions id:{}", userId);
        try {
            UserCourseExecutionsDto result = monolithService.getUserCourseExecutions(userId);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
