package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.FillInSpotDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CodeFillIn)
public class CodeFillInQuestion extends CodeQuestion {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question",fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<FillInSpot> fillInSpots = new ArrayList<>();

    public CodeFillInQuestion() {

    }

    public CodeFillInQuestion(Course course, CodeFillInQuestionDto questionDto) {
        super(course, questionDto);
        setFillInSpots(questionDto.getFillInSpots());
    }

    public List<FillInSpot> getFillInSpots() {
        return fillInSpots;
    }

    public void setFillInSpots(List<FillInSpotDto> fillInSpots){
        // todo add exception if none are given.
        // todo set fill in spots
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    @Override
    public void visitOptions(Visitor visitor) {
        for (FillInSpot spot : this.getFillInSpots()) {
            spot.accept(visitor);
        }
    }
}
