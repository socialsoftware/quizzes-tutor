package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementOptionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDetailsDto;

import java.util.List;

public class MultipleChoiceStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<StatementOptionDto> options;

    public MultipleChoiceStatementQuestionDetailsDto() {}

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementQuestionDetailsDto{" +
                "options=" + options +
                '}';
    }
}