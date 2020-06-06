package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;
import java.util.List;

public class FillInSpotDto implements Serializable {
    private Integer id;
    private List<OptionDto> options;

    public FillInSpotDto() {
    }

    public FillInSpotDto(FillInSpot option) {
        this.id = option.getId();
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