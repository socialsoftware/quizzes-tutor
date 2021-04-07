package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.OptionDto;

import java.util.ArrayList;
import java.util.List;

public class CodeFillInAnswerDto extends AnswerDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    public CodeFillInAnswerDto() {
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
