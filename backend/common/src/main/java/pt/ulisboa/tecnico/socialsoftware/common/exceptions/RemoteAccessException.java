package pt.ulisboa.tecnico.socialsoftware.common.exceptions;

public class RemoteAccessException extends RuntimeException {
    public RemoteAccessException() {
        super();
    }

    public RemoteAccessException(String message) {
        super(message);
    }
}
