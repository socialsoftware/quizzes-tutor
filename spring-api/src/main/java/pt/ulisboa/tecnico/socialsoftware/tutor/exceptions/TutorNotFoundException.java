package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TutorNotFoundException extends TutorException {

    public TutorNotFoundException(ExceptionError error, String value) {
        super(error,value);
    }
}
