package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceStatementQuestionDto extends StatementQuestionDto {
    private List<StatementOptionDto> options;

    public MultipleChoiceStatementQuestionDto(MultipleChoiceQuestionAnswer questionAnswer) {
        super(questionAnswer);
        this.options = ((MultipleChoiceQuestion)questionAnswer.getQuizQuestion().getQuestion()).getOptions().stream().map(StatementOptionDto::new).collect(Collectors.toList());
    }

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "StatementMultipleChoiceQuestionDto{" +
                ", content='" + getContent() + '\'' +
                ", options=" + getOptions() +
                ", image=" + getImage() +
                ", sequence=" + getSequence() +
                '}';
    }
}