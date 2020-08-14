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
        TutorException cause;

        private Error(String message, TutorException cause) {
            this.message = message;
            this.cause = cause;
        }

    }

    public Notification() {}

    private List<Error> errors = new ArrayList<>();
    private String errorMessage = "";

    public void addError(String errorMessage, TutorException e) {
        errors.add(new Error(errorMessage, e));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void generateErrorMessage() {
        errorMessage = errors.stream()
                .map(e -> e.message)
                .collect(Collectors.joining(", "));
    }

    public List<TutorException> getExceptions() {
        return errors.stream().map(error -> error.cause).collect(Collectors.toList());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
