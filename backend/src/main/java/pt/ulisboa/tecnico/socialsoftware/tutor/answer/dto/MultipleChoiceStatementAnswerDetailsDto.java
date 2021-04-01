package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import javax.persistence.Transient;

public class MultipleChoiceStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private Integer optionId;

    public MultipleChoiceStatementAnswerDetailsDto() {
    }

    public MultipleChoiceStatementAnswerDetailsDto(MultipleChoiceAnswer questionAnswer) {
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

    @Transient
    private MultipleChoiceAnswer createdMultipleChoiceAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdMultipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdMultipleChoiceAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return optionId == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new MultipleChoiceAnswerItem(username, quizId, statementAnswerDto, this);
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
