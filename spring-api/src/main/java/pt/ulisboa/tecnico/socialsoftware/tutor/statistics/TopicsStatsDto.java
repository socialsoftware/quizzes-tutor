package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

public class TopicsStatsDto {
    private String topicName;
    private Integer uniqueCorrectQuestions;
    private Integer uniqueWrongQuestions;
    private Integer totalUniqueQuestions;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getUniqueCorrectQuestions() {
        return uniqueCorrectQuestions;
    }

    public void setUniqueCorrectQuestions(Integer uniqueCorrectQuestions) {
        this.uniqueCorrectQuestions = uniqueCorrectQuestions;
    }

    public Integer getUniqueWrongQuestions() {
        return uniqueWrongQuestions;
    }

    public void setUniqueWrongQuestions(Integer uniqueWrongQuestions) {
        this.uniqueWrongQuestions = uniqueWrongQuestions;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }
}
