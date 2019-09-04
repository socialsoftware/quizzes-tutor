package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TutorException extends RuntimeException {
    public enum ExceptionError {
        QUIZ_NOT_FOUND, QUIZ_ANSWER_NOT_FOUND, USER_MISMATCH, NOT_ENOUGH_QUESTIONS,
        QUIZ_MISMATCH, OPTION_NOT_FOUND, USER_NOT_FOUND, QUESTION_NOT_FOUND,
        QUESTION_MISSING_DATA, QUESTION_MULTIPLE_CORRECT_OPTIONS, QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS,
        QUESTION_IS_USED_IN_QUIZ, DUPLICATE_TOPIC, TOPIC_NOT_FOUND,
        DUPLICATE_USER, USERS_IMPORT_ERROR, QUESTIONS_IMPORT_ERROR, TOPICS_IMPORT_ERROR, QUIZZES_IMPORT_ERROR, QUIZ_HAS_ANSWERS, QUIZ_QUESTION_HAS_ANSWERS, ANSWERS_IMPORT_ERROR,
        FENIX_ERROR
    }

    private final ExceptionError error;
    private final String value;

    public TutorException(ExceptionError error, String value) {
        super(error.name() + " for value: " + value);
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
