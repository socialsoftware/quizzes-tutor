package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongParametersException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public WrongParametersException(String error) {
        super(error);
        logger.error(error);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}