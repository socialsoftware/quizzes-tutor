package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class StatementFillInSpotsDto implements Serializable {
    public Integer sequence;
    public List<StatementOptionDto> options;

    public StatementFillInSpotsDto(FillInSpot fillInSpot) {
        this.sequence = fillInSpot.getSequence();
        this.options = fillInSpot.getOptions().stream().map(StatementOptionDto::new).collect(Collectors.toList());
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "StatementFillInSpotsDto{" +
                "sequence=" + getSequence() +
                ", options=" + getOptions() +
                '}';
    }
}