package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInQuestion extends QuestionDetails {

    private String language;

    @Column(columnDefinition = "TEXT")
    private String code;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails",fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<FillInSpot> fillInSpots = new ArrayList<>();


    public CodeFillInQuestion() {
        super();
    }

    public CodeFillInQuestion(Question question, CodeFillInQuestionDto codeFillInQuestionDto) {
        super(question);
        update(codeFillInQuestionDto);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FillInSpot> getFillInSpots() {
        return fillInSpots;
    }

    public void setFillInSpots(List<FillInSpotDto> fillInSpots){
        if (fillInSpots.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_OPTION_NEEDED);
        }

        for (FillInSpotDto fillInSpotDto : fillInSpots) {
            if (fillInSpotDto.getId() == null) {
                FillInSpot fillInSpot = new FillInSpot(fillInSpotDto);
                fillInSpot.setQuestionDetails(this);
                this.fillInSpots.add(fillInSpot);
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
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new CodeFillInCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new CodeFillInStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new CodeFillInStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new CodeFillInAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new CodeFillInQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (var spot : this.fillInSpots) {
            spot.delete();
        }
        this.fillInSpots.clear();
    }

    @Override
    public String getCorrectAnswerText() {
        return String.format("%d/%d", this.getFillInSpots().size(), this.getFillInSpots().size());
    }

    public void update(CodeFillInQuestionDto questionDetails) {
        setCode(questionDetails.getCode());
        setLanguage(questionDetails.getLanguage());
        setFillInSpots(questionDetails.getFillInSpots());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }
}
