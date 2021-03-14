package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.question;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    /*@Override
    public void update(MultipleChoiceQuestion question) {
        question.update(this);
    }*/

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}
