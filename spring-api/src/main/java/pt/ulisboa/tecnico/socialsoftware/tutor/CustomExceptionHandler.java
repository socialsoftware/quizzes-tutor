package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImportExportController;
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ImportExportController.class);

    @Autowired
    private LogService logService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> exception(Exception e) {
        logService.create(null, LocalDateTime.now(), e.getMessage());
        logger.error(e.getMessage());
        return null;
    }
}