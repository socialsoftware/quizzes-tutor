package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<StatementOptionDto> options;

    public MultipleChoiceStatementQuestionDetailsDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream()
                .map(StatementOptionDto::new)
                .collect(Collectors.toList());
    }

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    // TODO[is->has]: update to string
    /*
    @Override
    public String toString() {
        return "StatementMultipleChoiceQuestionDto{" +
                ", content='" + getContent() + '\'' +
                ", options=" + getOptions() +
                ", image=" + getImage() +
                ", sequence=" + getSequence() +
                '}';
    }
    */
}