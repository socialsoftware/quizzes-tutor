package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInSpotDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private List<OptionDto> options = new ArrayList<>();

    public CodeFillInSpotDto() {
    }

    public CodeFillInSpotDto(CodeFillInSpot option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.options = option.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "FillInSpotDto{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", options=" + options +
                '}';
    }
}
