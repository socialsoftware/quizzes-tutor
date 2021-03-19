package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.dtos.question.*;
import pt.ulisboa.tecnico.socialsoftware.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInQuestion extends QuestionDetails {

    private Languages language;

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

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
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
        return this.getDto();
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
        return this.codeFillInSpots.stream()
                .map(x ->
                        String.valueOf(
                                x.getOptions().stream()
                                        .filter(CodeFillInOption::isCorrect)
                                        .findAny()
                                        .get()
                                        .getSequence() + 1))
                .collect(Collectors.joining(" | "));
    }

    @Override
    public void update(QuestionDetailsDto questionDetailsDto) {
        if (questionDetailsDto instanceof CodeFillInQuestionDto) {
            CodeFillInQuestionDto codeFillInQuestionDto = (CodeFillInQuestionDto) questionDetailsDto;
            setCode(codeFillInQuestionDto.getCode());
            setLanguage(codeFillInQuestionDto.getLanguage());
            setFillInSpots(codeFillInQuestionDto.getFillInSpots());
        }
        else {
            throw new TutorException(INVALID_QUESTION_DETAILS_DTO, questionDetailsDto.getClass().getName());
        }
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

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        var result = new ArrayList<String>();
        var orderSpots = getFillInSpots().stream().sorted(Comparator.comparing(CodeFillInSpot::getSequence)).collect(Collectors.toList());
        for (var spots: orderSpots) {
            var option = spots.getOptions().stream().filter(x -> selectedIds.contains(x.getId())).findAny();
            if (option.isPresent()){
                result.add(String.format("%s", option.get().getSequence() + 1));
            }
            else {
                result.add("-");
            }
        }
        return String.join(" | ", result);
    }

    public CodeFillInQuestionDto getDto() {
        CodeFillInQuestionDto dto = new CodeFillInQuestionDto();
        dto.setLanguage(getLanguage());
        dto.setCode(getCode());
        dto.setFillInSpots(getFillInSpots()
                .stream().map(CodeFillInSpot::getDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
