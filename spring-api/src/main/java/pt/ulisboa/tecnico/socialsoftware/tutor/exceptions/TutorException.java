package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TutorException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(TutorException.class);
    private final ExceptionError error;

    public TutorException(ExceptionError error) {
        super(error.label);
        logger.error(error.label);
        this.error = error;
    }

    public TutorException(ExceptionError error, String value) {
        super(String.format(error.label, value));
        logger.error(String.format(error.label, value));
        this.error = error;
    }

    public TutorException(ExceptionError error, String value1, String value2) {
        super(String.format(error.label, value1, value2));
        logger.error(String.format(error.label, value1, value2));
        this.error = error;
    }

    public TutorException(ExceptionError error, int value) {
        super(String.format(error.label, value));
        logger.error(String.format(error.label, value));
        this.error = error;
    }

    public TutorException(ExceptionError error, int value1, int value2) {
        super(String.format(error.label, value1, value2));
        logger.error(String.format(error.label, value1, value2));
        this.error = error;
    }

    public ExceptionError getError() {
        return error;
    }
}
