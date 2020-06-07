package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FillInSpotDto implements Serializable {
    private Integer id;
    private List<OptionDto> options = new ArrayList<>();

    public FillInSpotDto() {
    }

    public FillInSpotDto(FillInSpot option) {
        this.id = option.getId();
        this.options = option.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "FillInSpotDto {" +
                "id=" + getId() +
                ", options=" + getOptions() +
                '}';
    }
}