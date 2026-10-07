package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** A topic as a node of the course's topic tree, with the document sections it is taught from. Teachers only. */
public class TopicNodeDto implements Serializable {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer sequence;
    private Integer numberOfQuestions;
    private List<TopicSourceDto> sources = new ArrayList<>();

    public TopicNodeDto() {
    }

    public TopicNodeDto(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.parentId = topic.getParentId();
        this.sequence = topic.getSequence();
        this.numberOfQuestions = topic.getQuestions().size();
        this.sources = topic.getSources().stream().map(TopicSourceDto::new).collect(Collectors.toList());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getParentId() { return parentId; }

    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Integer getSequence() { return sequence; }

    public void setSequence(Integer sequence) { this.sequence = sequence; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }

    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public List<TopicSourceDto> getSources() { return sources; }

    public void setSources(List<TopicSourceDto> sources) { this.sources = sources; }
}
