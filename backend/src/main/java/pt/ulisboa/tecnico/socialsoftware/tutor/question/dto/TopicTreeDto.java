package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A tree of topics to create in one go: what the service proposes for a document and what the
 * teacher sends back after editing it. Nodes refer to their parent by `key`, not by id, because
 * most of them do not exist yet. Nothing is stored until the teacher saves it.
 */
public class TopicTreeDto implements Serializable {
    private List<Node> nodes = new ArrayList<>();

    public TopicTreeDto() {
    }

    public TopicTreeDto(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() { return nodes; }

    public void setNodes(List<Node> nodes) { this.nodes = nodes; }

    public static class Node implements Serializable {
        private String key;
        private String name;
        private String parentKey;
        // Set when a topic with this name already exists in the course: the node then only adds its sources to it
        private Integer existingTopicId;
        // Only informs the teacher; ignored when saving
        private Integer chunkCount;
        private List<TopicSourceDto> sources = new ArrayList<>();

        public Node() {
        }

        public Node(String key, String name, String parentKey) {
            this.key = key;
            this.name = name;
            this.parentKey = parentKey;
        }

        public String getKey() { return key; }

        public void setKey(String key) { this.key = key; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getParentKey() { return parentKey; }

        public void setParentKey(String parentKey) { this.parentKey = parentKey; }

        public Integer getExistingTopicId() { return existingTopicId; }

        public void setExistingTopicId(Integer existingTopicId) { this.existingTopicId = existingTopicId; }

        public Integer getChunkCount() { return chunkCount; }

        public void setChunkCount(Integer chunkCount) { this.chunkCount = chunkCount; }

        public List<TopicSourceDto> getSources() { return sources; }

        public void setSources(List<TopicSourceDto> sources) { this.sources = sources; }
    }
}
