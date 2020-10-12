package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class CodeFillInQuestion extends QuestionDetails {

    private String language;

    @Column(columnDefinition = "TEXT")
    private String code;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question",fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<FillInSpot> fillInSpots = new ArrayList<>();


    public CodeFillInQuestion() {
        super();
    }

    public CodeFillInQuestion(Question question, CodeFillInQuestionDto codeFillInQuestionDto) {
        super(question);
        setFillInSpots(codeFillInQuestionDto.getFillInSpots());
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

    // TODO: Add missing DTOs
    public void setFillInSpots(List<FillInSpotDto> fillInSpots){
        if (fillInSpots.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_OPTION_NEEDED);
        }

        int index = 0;
        for (FillInSpotDto fillInSpotDto : fillInSpots) {
            if (fillInSpotDto.getId() == null) {
                FillInSpot fillInSpot = new FillInSpot(fillInSpotDto);
                fillInSpot.setQuestion(this);
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
    public Integer getCorrectAnswer() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public void update(Updator updator) {
        // TODO: IMPLEMENT
    }

    @Override
    public String getCorrectAnswerText() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO: IMPLEMENT
    }
}
