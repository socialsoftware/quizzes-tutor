package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ACCESS_DENIED;

// https://www.toptal.com/java/spring-boot-rest-api-error-handling

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger myLogger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(TutorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TutorExceptionDto tutorException(TutorException e) {
        return new TutorExceptionDto(e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public TutorExceptionDto accessDeniedException(AccessDeniedException e) {
        myLogger.error(e.getMessage());
        return new TutorExceptionDto(ACCESS_DENIED);
    }

    @ExceptionHandler(LockAcquisitionException.class)
    @ResponseStatus(HttpStatus.OK)
    public TutorExceptionDto lockAcquisitionException(LockAcquisitionException e) {
        myLogger.error("LockAcquisitionException");
        return new TutorExceptionDto(e);
    }

    @ExceptionHandler(ClientAbortException.class)
    @ResponseStatus(HttpStatus.OK)
    public void clientAbortException(ClientAbortException e) {
        // Ignore my broken pipe. It still works
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public TutorExceptionDto randomException(Exception e) {
        myLogger.error(e.getMessage(), e);
        return new TutorExceptionDto(e);
    }
}