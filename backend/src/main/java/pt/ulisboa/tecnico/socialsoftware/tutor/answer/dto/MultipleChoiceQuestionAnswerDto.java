package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

public class MultipleChoiceQuestionAnswerDto extends QuestionAnswerDto {
    private OptionDto option;

    public MultipleChoiceQuestionAnswerDto() {
    }

    public MultipleChoiceQuestionAnswerDto(MultipleChoiceQuestionAnswer questionAnswer) {
        super(questionAnswer);
        if (questionAnswer.getOption() != null)
            this.option = new OptionDto(questionAnswer.getOption());
    }

    public OptionDto getOption() {
        return option;
    }

    public void setOption(OptionDto option) {
        this.option = option;
    }
}
