package com.example.tutor.stats;

public class AnsweredQuestionDTO {
        private String content;
        private Integer order;
        private Integer answeredOption;
        private Integer correctOption;
        private AnsweredOptionDTO[] options;

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

    public AnsweredOptionDTO[] getOptions() {
        return options;
    }

    public void setOptions(AnsweredOptionDTO[] options) {
        this.options = options;
    }
}
