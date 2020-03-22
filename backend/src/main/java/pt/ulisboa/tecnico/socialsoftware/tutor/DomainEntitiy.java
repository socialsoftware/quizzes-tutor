package pt.ulisboa.tecnico.socialsoftware.tutor;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

public interface DomainEntitiy {
    public void accept(Visitor visitor);
}
