package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.OptionDto;

public class MultipleChoiceAnswerDto extends AnswerDetailsDto {
    private OptionDto option;

    public MultipleChoiceAnswerDto() {
    }

    public OptionDto getOption() {
        return option;
    }

    public void setOption(OptionDto option) {
        this.option = option;
    }
}
