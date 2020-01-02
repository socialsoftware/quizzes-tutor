package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImportExportController;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ImportExportController.class);

    // Prevent the same error from cluttering the logs
    private List errorList = new ArrayList<>();

    @ExceptionHandler(TutorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public TutorException tutorException(TutorException e) {
        return e;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Exception exception(Exception e) {
        if (!errorList.contains(e.getMessage())) {
            errorList.add(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}