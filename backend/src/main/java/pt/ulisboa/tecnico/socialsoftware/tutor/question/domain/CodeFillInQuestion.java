package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInSpotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FILL_IN_SPOT_NOT_FOUND;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInQuestion extends QuestionDetails {

    private String language;

    @Column(columnDefinition = "TEXT")
    private String code;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CodeFillInSpot> codeFillInSpots = new ArrayList<>();


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

    public List<CodeFillInSpot> getFillInSpots() {
        return codeFillInSpots;
    }

    public void setFillInSpots(List<CodeFillInSpotDto> fillInSpots) {
        if (fillInSpots.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_OPTION_NEEDED);
        }

        for (CodeFillInSpotDto codeFillInSpotDto : fillInSpots) {
            if (codeFillInSpotDto.getId() == null) {
                CodeFillInSpot codeFillInSpot = new CodeFillInSpot(codeFillInSpotDto);
                codeFillInSpot.setQuestionDetails(this);
                this.codeFillInSpots.add(codeFillInSpot);
            } else {
                CodeFillInSpot option = getFillInSpots()
                        .stream()
                        .filter(op -> op.getId().equals(codeFillInSpotDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(FILL_IN_SPOT_NOT_FOUND, codeFillInSpotDto.getId()));

                option.setOptions(codeFillInSpotDto.getOptions());
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
        for (var spot : this.codeFillInSpots) {
            spot.delete();
        }
        this.codeFillInSpots.clear();
    }

    @Override
    public String getCorrectAnswerRepresentation() {
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

    public void visitFillInSpots(Visitor visitor) {
        for (var spot : this.getFillInSpots()) {
            spot.accept(visitor);
        }
    }
}
