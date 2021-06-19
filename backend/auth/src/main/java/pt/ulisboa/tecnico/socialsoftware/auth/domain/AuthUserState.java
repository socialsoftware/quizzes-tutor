package pt.ulisboa.tecnico.socialsoftware.auth.domain;

public enum AuthUserState {
    APPROVED,
    READY_FOR_UPDATE,
    APPROVAL_PENDING,
    REJECTED,
    UPDATE_PENDING
}
