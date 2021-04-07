package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;

public class StatementOptionDto implements Serializable {
    private Integer optionId;
    private String content;

    public StatementOptionDto() {}

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "StatementOptionDto{" +
                "optionId=" + optionId +
                ", content='" + content + '\'' +
                '}';
    }
}