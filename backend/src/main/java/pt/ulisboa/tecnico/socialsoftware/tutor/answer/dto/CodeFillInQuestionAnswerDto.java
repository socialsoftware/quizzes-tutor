package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.FillInSpotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CodeFillInQuestionAnswerDto extends QuestionAnswerDto {
    private List<OptionDto> options;

    // todo might be missing relation with spots

    public CodeFillInQuestionAnswerDto() {
    }

    public CodeFillInQuestionAnswerDto(CodeFillInQuestionAnswer questionAnswer) {
        super(questionAnswer);
        if (questionAnswer.getFillInOptions() != null)
            this.options = questionAnswer.getFillInOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
