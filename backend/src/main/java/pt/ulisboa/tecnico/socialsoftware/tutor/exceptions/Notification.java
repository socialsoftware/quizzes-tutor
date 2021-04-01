package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Notification implements Serializable {

    private static class Error extends RuntimeException {
        final String message;
        final TutorException cause;

        private Error(String message, TutorException cause) {
            this.message = message;
            this.cause = cause;
        }

    }

    public Notification() {}

    private List<Error> errors = new ArrayList<>();

    public void addError(String errorMessage, TutorException e) {
        errors.add(new Error(errorMessage, e));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String errorMessage() {
        return errors.stream()
                .map(e -> e.message)
                .collect(Collectors.joining(", "));
    }

    public List<String> getErrorMessages() {
        return errors.stream().map(error -> error.message).collect(Collectors.toList());
    }

    public List<TutorException> getExceptions() {
        return errors.stream().map(error -> error.cause).collect(Collectors.toList());
    }

}
