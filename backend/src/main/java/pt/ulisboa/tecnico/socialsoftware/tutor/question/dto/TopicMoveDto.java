package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;

/** Where a topic goes in the tree: under `parentId` (null: at the top), optionally at a given position. */
public class TopicMoveDto implements Serializable {
    private Integer parentId;
    private Integer sequence;

    public TopicMoveDto() {
    }

    public Integer getParentId() { return parentId; }

    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Integer getSequence() { return sequence; }

    public void setSequence(Integer sequence) { this.sequence = sequence; }
}
