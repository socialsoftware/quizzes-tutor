package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CodeFillIn)
public class CodeFillInQuestion extends CodeQuestion {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question",fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<FillInSpot> answerSpots = new ArrayList<>();


    @Override
    public void visitDependencies(Visitor visitor) {
        throw new TutorException(ErrorMessage.ACCESS_DENIED);
    }
}
