package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Languages;

import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private Languages language;
    private String code;
    private List<StatementFillInSpotsDto> fillInSpots;

    public CodeFillInStatementQuestionDetailsDto(CodeFillInQuestion question) {
        this.code = question.getCode();
        this.language = question.getLanguage();
        this.fillInSpots = question.getFillInSpots().stream().map(StatementFillInSpotsDto::new).collect(Collectors.toList());
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