package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.FillInSpotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

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
        if (fillInSpots.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_OPTION_NEEDED);
        }

        int index = 0;
        for (FillInSpotDto fillInSpotDto : fillInSpots) {
            if (fillInSpotDto.getId() == null) {
                new FillInSpot(fillInSpotDto).setQuestion(this);
            } else {
                FillInSpot option = getFillInSpots()
                        .stream()
                        .filter(op -> op.getId().equals(fillInSpotDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(FILL_IN_SPOT_NOT_FOUND, fillInSpotDto.getId()));

                option.setOptions(fillInSpotDto.getOptions());
            }
        }
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
