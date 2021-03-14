package pt.ulisboa.tecnico.socialsoftware.dtos.question;

import java.util.ArrayList;
import java.util.List;

public class CodeFillInQuestionDto extends QuestionDetailsDto {
    private Languages language;

    private String code;

    private List<CodeFillInSpotDto> fillInSpots = new ArrayList<>();

    public CodeFillInQuestionDto() {
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

    /*@Override
    public void update(CodeFillInQuestion question) {
        question.update(this);
    }*/
}
