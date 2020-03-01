package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

interface TutorExceptionSubError extends Serializable{
}

public class TutorExceptionDto  implements TutorExceptionSubError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<TutorExceptionSubError> subErrors;


    TutorExceptionDto(Throwable ex) {
        this.timestamp = LocalDateTime.now();
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }


    public TutorExceptionDto(TutorException e) {
        this.timestamp = LocalDateTime.now();
        this.message = e.getMessage();
    }

    public TutorExceptionDto(ErrorMessage errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.message = errorMessage.label;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<TutorExceptionSubError> getSubErrors() {
        return subErrors;
    }

    public void setSubErrors(List<TutorExceptionSubError> subErrors) {
        this.subErrors = subErrors;
    }
}