package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CodeFillIn)
public class CodeFillInQuestion extends CodeQuestion {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question",fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<FillInSpot> answerSpots = new ArrayList<>();

    public CodeFillInQuestion() {

    }

    // todo add code fill question dto.
    public CodeFillInQuestion(Course course, QuestionDto questionDto) {
        super(course, questionDto);
        //setAnswerSpots(questionDto.getOptions());
    }

    public List<FillInSpot> getAnswerSpots() {
        return answerSpots;
    }

    // todo setAnswerSpots from DTO

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    @Override
    public void visitOptions(Visitor visitor) {
        for (FillInSpot spot : this.getAnswerSpots()) {
            spot.accept(visitor);
        }
    }
}
