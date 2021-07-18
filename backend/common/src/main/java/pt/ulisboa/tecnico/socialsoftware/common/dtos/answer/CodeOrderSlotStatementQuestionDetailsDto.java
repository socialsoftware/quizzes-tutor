package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;

public class CodeOrderSlotStatementQuestionDetailsDto implements Serializable {
    private Integer id;
    private String content;

    public CodeOrderSlotStatementQuestionDetailsDto() {}

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
