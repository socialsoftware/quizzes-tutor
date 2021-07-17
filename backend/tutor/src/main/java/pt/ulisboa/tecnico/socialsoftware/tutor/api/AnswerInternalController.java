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
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;

@RestController
@RequestMapping(value = "/rest/answers")
public class AnswerInternalController {
    private static final Logger logger = LoggerFactory.getLogger(AnswerInternalController.class);

    @Autowired
    MonolithService monolithService;

    @RequestMapping(value = "/getStatementQuiz", method = RequestMethod.GET)
    public ResponseEntity<StatementQuizDto> getStatementQuiz(@RequestParam Integer userId, @RequestParam Integer quizId) {
        logger.info("getStatementQuiz userId:{} quizId:{}", userId, quizId);
        try {
            StatementQuizDto result = monolithService.getStatementQuiz(userId, quizId);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/startQuiz", method = RequestMethod.GET)
    public ResponseEntity<StatementQuizDto> startQuiz(@RequestParam Integer userId, @RequestParam Integer quizId) {
        logger.info("startQuiz userId:{} quizId:{}", userId, quizId);
        try {
            StatementQuizDto result = monolithService.startQuiz(userId, quizId);
            logger.info("Result:{}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception:{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
