package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;

/**
 * A teacher's request for questions about a topic of the course. They are written from the pieces
 * of documents under the topic and under its subtopics.
 */
public class GenerationRequestDto implements Serializable {
    private Integer topicId;
    private String difficulty;
    private Integer count;
    private String groundingMode;
    private String language;
    // What to ask about inside the topic, e.g. "sign rules of the product"
    private String focus;

    public GenerationRequestDto() {
    }

    public Integer getTopicId() { return topicId; }

    public void setTopicId(Integer topicId) { this.topicId = topicId; }

    public String getDifficulty() { return difficulty; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getCount() { return count; }

    public void setCount(Integer count) { this.count = count; }

    public String getGroundingMode() { return groundingMode; }

    public void setGroundingMode(String groundingMode) { this.groundingMode = groundingMode; }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public String getFocus() { return focus; }

    public void setFocus(String focus) { this.focus = focus; }
}
