package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

public interface DomainEntity {
    void accept(Visitor visitor);
}
