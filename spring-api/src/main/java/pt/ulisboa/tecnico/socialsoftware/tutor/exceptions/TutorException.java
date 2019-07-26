package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public class TutorException extends RuntimeException {
    public enum ExceptionError {QUIZ_NOT_FOUND, QUIZ_ANSWER_NOT_FOUND, USER_MISMATCH, QUIZ_QUESTION_NOT_FOUND, QUIZ_MISMATCH, OPTION_NOT_FOUND, USER_NOT_FOUND, QUESTION_MISMATCH, QUIZ_ALREADY_ANSWERED, QUESTION_NOT_FOUND};
    private final ExceptionError error;
    private final String value;

    public TutorException(ExceptionError error, String value) {
        this.error = error;
        this.value = value;
    }
    public ExceptionError getError() {
        return error;
    }

    public String getValue() {
        return value;
    }
}
