package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Topics to create in one go, each with the pieces of documents to put under it: what the teacher
 * sends after distributing a document over the topic tree. Nodes refer to their parent by `key`,
 * not by id, because some of them do not exist yet; a node with `existingTopicId` stands for a
 * topic the course already has.
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
        // Set when the node is a topic the course already has: it then only gets the node's pieces
        private Integer existingTopicId;
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

        public List<TopicSourceDto> getSources() { return sources; }

        public void setSources(List<TopicSourceDto> sources) { this.sources = sources; }
    }
}
