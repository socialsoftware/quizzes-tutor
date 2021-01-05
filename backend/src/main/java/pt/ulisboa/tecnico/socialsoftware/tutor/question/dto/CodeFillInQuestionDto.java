package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Languages;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInQuestionDto extends QuestionDetailsDto {
    private Languages language;

    private String code;

    private List<CodeFillInSpotDto> fillInSpots = new ArrayList<>();

    public CodeFillInQuestionDto() {
    }

    public CodeFillInQuestionDto(CodeFillInQuestion question) {
        this.language = question.getLanguage();
        this.code = question.getCode();
        this.fillInSpots = question.getFillInSpots()
                .stream().map(CodeFillInSpotDto::new)
                .collect(Collectors.toList());
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CodeFillInSpotDto> getFillInSpots() {
        return fillInSpots;
    }

    public void setFillInSpots(List<CodeFillInSpotDto> fillInSpots) {
        this.fillInSpots = fillInSpots;
    }

    @Override
    public String toString() {
        return "CodeFillInQuestionDto{" +
                "language='" + language + '\'' +
                ", code='" + code + '\'' +
                ", fillInSpots=" + fillInSpots +
                '}';
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new CodeFillInQuestion(question, this);
    }

    @Override
    public void update(CodeFillInQuestion question) {
        question.update(this);
    }
}
