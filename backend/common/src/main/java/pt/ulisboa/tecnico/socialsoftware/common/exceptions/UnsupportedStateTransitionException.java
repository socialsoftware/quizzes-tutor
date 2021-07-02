package pt.ulisboa.tecnico.socialsoftware.common.exceptions;

/**
 * The exception to indicate that the state transition is not supported.
 */
public class UnsupportedStateTransitionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedStateTransitionException(Enum<?> state) {
        super("current state: " + state);
    }
}
