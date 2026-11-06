package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A teacher's request for questions about one or more topics of the course. They are written from
 * the pieces of documents under the topics and under their subtopics.
 */
public class GenerationRequestDto implements Serializable {
    private List<Integer> topicIds = new ArrayList<>();
    private String difficulty;
    private Integer count;
    private String groundingMode;
    private String language;
    // What to ask about inside the topic, e.g. "sign rules of the product"
    private String focus;

    public GenerationRequestDto() {
    }

    public List<Integer> getTopicIds() { return topicIds; }

    public void setTopicIds(List<Integer> topicIds) { this.topicIds = topicIds == null ? new ArrayList<>() : new ArrayList<>(topicIds); }

    /** A request for one topic (what clients sent before there could be several). */
    public void setTopicId(Integer topicId) { this.topicIds = topicId == null ? new ArrayList<>() : new ArrayList<>(List.of(topicId)); }

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
