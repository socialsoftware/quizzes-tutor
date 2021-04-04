package pt.ulisboa.tecnico.socialsoftware.common.dtos.question;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {
        setType(QuestionTypes.MULTIPLE_CHOICE_QUESTION);
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}
