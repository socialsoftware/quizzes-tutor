package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CodeFillInCorrectAnswerDto extends CorrectAnswerDto {
    private List<CodeFillInSpotCorrectAnswerDto> correctOptions;

    public CodeFillInCorrectAnswerDto(CodeFillInQuestionAnswer questionAnswer) {
        super(questionAnswer);
        // todo improve code
        this.correctOptions = ((CodeFillInQuestion) questionAnswer.getQuizQuestion().getQuestion()).getFillInSpots()
                .stream()
                .map(CodeFillInSpotCorrectAnswerDto::new)
                .collect(Collectors.toList());
    }

    public List<CodeFillInSpotCorrectAnswerDto> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(List<CodeFillInSpotCorrectAnswerDto> correctOptions) {
        this.correctOptions = correctOptions;
    }

    @Override
    public String toString() {
        return "CodeFillInCorrectAnswerDto{" +
                "correctOptionId=" + getCorrectOptions() +
                ", sequence=" + getSequence() +
                '}';
    }
}