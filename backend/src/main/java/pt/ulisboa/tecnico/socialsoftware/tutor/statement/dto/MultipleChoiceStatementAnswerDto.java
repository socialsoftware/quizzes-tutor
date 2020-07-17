package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public class MultipleChoiceStatementAnswerDto extends StatementAnswerDetailsDto {
    private Integer optionId;

    public MultipleChoiceStatementAnswerDto() {
    }

    public MultipleChoiceStatementAnswerDto(MultipleChoiceAnswer questionAnswer) {
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

    private MultipleChoiceAnswer createdMultipleChoiceAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdMultipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestion().update(this);
        return createdMultipleChoiceAnswer;
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        createdMultipleChoiceAnswer.setOption(question, this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementAnswerDto{" +
                "optionId=" + optionId +
                '}';
    }
}
