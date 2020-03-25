package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

public interface DomainEntity {
    void accept(Visitor visitor);
}
