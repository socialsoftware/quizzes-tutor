package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssessmentDto implements Serializable {
    private Integer id;
    private String title;
    private String status;
    private List<TopicConjunctionDto> topicConjunctions = new ArrayList<>();

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        this.id = assessment.getId();
        this.id = assessment.getNumber();
        this.title = assessment.getTitle();
        this.status = assessment.getStatus().name();
        this.topicConjunctions = assessment.getTopicConjunctions().stream().map(TopicConjunctionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return id;
    }

    public void setNumber(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TopicConjunctionDto> getTopicConjunctions() {
        return topicConjunctions;
    }

    public void setTopicConjunctions(List<TopicConjunctionDto> topicConjunctions) {
        this.topicConjunctions = topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunctionDto topicConjunctionDto) {
        this.topicConjunctions.add(topicConjunctionDto);
    }


    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", topicConjunctions=" + topicConjunctions +
                '}';
    }
}