package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<CodeFillInSpotCorrectAnswerDto> correctOptions;

    public CodeFillInCorrectAnswerDto(CodeFillInQuestion question) {
        this.correctOptions = question.getFillInSpots()
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
}