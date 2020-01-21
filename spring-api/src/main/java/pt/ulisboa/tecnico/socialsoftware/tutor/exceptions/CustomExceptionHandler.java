package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImportExportController;

import org.springframework.security.access.AccessDeniedException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ACCESS_DENIED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.UNEXPECTED_ERROR;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ImportExportController.class);

    @ExceptionHandler(TutorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TutorException tutorException(TutorException e) {
        return e;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public TutorException accessDeniedException(AccessDeniedException e) {
        return new TutorException(ACCESS_DENIED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public TutorException randomException(Exception e) {
        logger.error(e.getMessage(), e);
        return new TutorException(UNEXPECTED_ERROR);
    }
}