package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.Languages;

import java.util.List;

public class CodeFillInStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private Languages language;
    private String code;
    private List<StatementFillInSpotsDto> fillInSpots;

    public CodeFillInStatementQuestionDetailsDto() {}

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

    public List<StatementFillInSpotsDto> getFillInSpots() {
        return fillInSpots;
    }

    public void setFillInSpots(List<StatementFillInSpotsDto> fillInSpots) {
        this.fillInSpots = fillInSpots;
    }

    @Override
    public String toString() {
        return "CodeFillInStatementQuestionDetailsDto{" +
                "language='" + language + '\'' +
                ", code='" + code + '\'' +
                ", fillInSpots=" + fillInSpots +
                '}';
    }
}