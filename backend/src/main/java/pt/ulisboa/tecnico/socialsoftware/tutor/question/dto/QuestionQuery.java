package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;
import java.util.Set;

public class QuestionQuery implements Serializable {
    private String content;
    private Set<Integer> topics;
    private Set<String> status;
    private boolean clarificationsOnly;
    private boolean noAnswersOnly;
    private Integer[] difficulty;
    private String beginCreationDate;
    private String endCreationDate;

    public QuestionQuery() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Integer> getTopics() {
        return topics;
    }

    public void setTopics(Set<Integer> topics) {
        this.topics = topics;
    }

    public Set<String> getStatus() {
        return status;
    }

    public void setStatus(Set<String> status) {
        this.status = status;
    }

    public boolean isClarificationsOnly() {
        return clarificationsOnly;
    }

    public void setClarificationsOnly(boolean clarificationsOnly) {
        this.clarificationsOnly = clarificationsOnly;
    }

    public boolean isNoAnswersOnly() {
        return noAnswersOnly;
    }

    public void setNoAnswersOnly(boolean noAnswersOnly) {
        this.noAnswersOnly = noAnswersOnly;
    }

    public Integer[] getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer[] difficulty) {
        this.difficulty = difficulty;
    }

    public String getBeginCreationDate() {
        return beginCreationDate;
    }

    public void setBeginCreationDate(String beginCreationDate) {
        this.beginCreationDate = beginCreationDate;
    }

    public String getEndCreationDate() {
        return endCreationDate;
    }

    public void setEndCreationDate(String endCreationDate) {
        this.endCreationDate = endCreationDate;
    }
}
