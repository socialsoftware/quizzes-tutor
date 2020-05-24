package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;

public class MultipleChoiceStatementAnswerDto extends StatementAnswerDto {
    private Integer optionId;

    public MultipleChoiceStatementAnswerDto() {
    }

    public MultipleChoiceStatementAnswerDto(MultipleChoiceQuestionAnswer questionAnswer) {
        super(questionAnswer);
        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                ", optionId=" + getOptionId() +
                ", timeTaken=" + getTimeTaken() +
                ", sequence=" + getSequence() +
                '}';
    }
}
