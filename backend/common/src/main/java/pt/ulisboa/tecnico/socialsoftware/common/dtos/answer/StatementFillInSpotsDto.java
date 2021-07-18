package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;
import java.util.List;

public class StatementFillInSpotsDto implements Serializable {
    private Integer sequence;
    private List<StatementOptionDto> options;

    public StatementFillInSpotsDto() {}

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