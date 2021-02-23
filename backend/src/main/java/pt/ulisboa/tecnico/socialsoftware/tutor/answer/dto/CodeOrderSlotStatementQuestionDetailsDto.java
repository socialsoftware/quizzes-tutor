package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import java.io.Serializable;

public class CodeOrderSlotStatementQuestionDetailsDto implements Serializable {
    private Integer id;
    private String content;

    public CodeOrderSlotStatementQuestionDetailsDto(CodeOrderSlot codeOrderSlot) {
        id = codeOrderSlot.getId();
        content = codeOrderSlot.getContent();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
