package pt.ulisboa.tecnico.socialsoftware.dtos.question;

import java.util.ArrayList;
import java.util.List;

public class CodeOrderQuestionDto extends QuestionDetailsDto {
    private Languages language;

    private List<CodeOrderSlotDto> codeOrderSlots = new ArrayList<>();

    public CodeOrderQuestionDto() {
        setType(QuestionTypes.CODE_ORDER_QUESTION);
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CodeOrderSlotDto> getCodeOrderSlots() {
        return codeOrderSlots;
    }

    public void setCodeOrderSlots(List<CodeOrderSlotDto> codeOrderSlots) {
        this.codeOrderSlots = codeOrderSlots;
    }

    @Override
    public String toString() {
        return "CodeOrderQuestionDto{" +
                "language='" + language + '\'' +
                ", fillInSpots=" + codeOrderSlots +
                '}';
    }
}
