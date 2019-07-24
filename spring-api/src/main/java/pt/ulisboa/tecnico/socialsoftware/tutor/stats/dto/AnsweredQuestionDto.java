package pt.ulisboa.tecnico.socialsoftware.tutor.stats.dto;

public class AnsweredQuestionDto {
        private String content;
        private Integer order;
        private Integer answeredOption;
        private Integer correctOption;
        private AnsweredOptionDto[] options;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getAnsweredOption() {
        return answeredOption;
    }

    public void setAnsweredOption(Integer answeredOption) {
        this.answeredOption = answeredOption;
    }

    public Integer getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(Integer correctOption) {
        this.correctOption = correctOption;
    }

    public AnsweredOptionDto[] getOptions() {
        return options;
    }

    public void setOptions(AnsweredOptionDto[] options) {
        this.options = options;
    }
}
