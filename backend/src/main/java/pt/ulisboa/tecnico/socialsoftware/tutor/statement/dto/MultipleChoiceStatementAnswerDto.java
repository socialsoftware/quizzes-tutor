package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerType;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;

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

    @Override
    public AnswerType getAnswerType(QuestionAnswer questionAnswer) {
        MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
        multipleChoiceAnswer.setOption(this);
        return multipleChoiceAnswer;
    }

    // TODO[is->has]: update to string
    /*
    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                "timeTaken=" + getTimeTaken() +
                ", sequence=" + getSequence() +
                ", questionAnswerId=" + getQuestionAnswerId() +
                ", optionId=" + getOptionId() +
                '}';
    }*/
}
