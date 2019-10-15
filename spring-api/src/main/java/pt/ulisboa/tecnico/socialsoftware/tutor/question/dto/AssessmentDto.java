package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AssessmentDto implements Serializable {
    private Integer id;
    private Integer number;
    private String title;
    private String status;
    private List<TopicConjuctionDto> topicConjuctions;

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        this.id = assessment.getId();
        this.number = assessment.getNumber();
        this.title = assessment.getTitle();
        this.status = assessment.getStatus().name();
        this.topicConjuctions = assessment.getTopicConjuctions().stream().map(TopicConjuctionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public List<TopicConjuctionDto> getTopicConjuctions() {
        return topicConjuctions;
    }

    public void setTopicConjuctions(List<TopicConjuctionDto> topicConjuctions) {
        this.topicConjuctions = topicConjuctions;
    }
}