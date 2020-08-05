package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Notification {
    private static class Error extends RuntimeException{
        String message;
        Exception cause;

        private Error(String message, Exception cause) {
            this.message = message;
            this.cause = cause;
        }

    }
    public Notification() {}

    private List<Error> errors = new ArrayList<>();

    public void addError(String errorMessage, Exception e) {
        errors.add(new Error(errorMessage, e));
    }

    public boolean hasErrors() {
        return ! errors.isEmpty();
    }

    public String errorMessage() {
        return errors.stream()
                .map(e -> e.message)
                .collect(Collectors.joining(", "));
    }
}
