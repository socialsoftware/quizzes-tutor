package pt.ulisboa.tecnico.socialsoftware.auth.domain;

public enum AuthUserState {
    READY_FOR_UPDATE,
    APPROVAL_PENDING,
    APPROVED,
    REJECTED,
    UPDATE_PENDING
}
