package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInStatementQuestionDto extends StatementQuestionDto {
    private String language;
    private String code;
    private List<StatementFillInSpotsDto> fillInSpots;

    public CodeFillInStatementQuestionDto(CodeFillInQuestionAnswer questionAnswer) {
        super(questionAnswer);
        CodeFillInQuestion question = (CodeFillInQuestion)questionAnswer.getQuizQuestion().getQuestion();
        this.code = question.getCode();
        this.language = question.getLanguage().name();
        this.fillInSpots = question.getFillInSpots().stream().map(StatementFillInSpotsDto::new).collect(Collectors.toList());
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
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
        return "StatementMultipleChoiceQuestionDto{" +
                ", content='" + getContent() + '\'' +
                ", fillInSpots=" + getFillInSpots() +
                ", image=" + getImage() +
                ", sequence=" + getSequence() +
                '}';
    }
}