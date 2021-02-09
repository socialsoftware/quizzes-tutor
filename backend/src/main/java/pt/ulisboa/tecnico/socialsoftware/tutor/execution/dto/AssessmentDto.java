package pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AssessmentDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private Integer numberOfQuestions;
    private String title;
    private String status;
    private List<TopicConjunctionDto> topicConjunctions;

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        this.id = assessment.getId();
        this.sequence = assessment.getSequence();
        this.numberOfQuestions = assessment.getQuestions().size();
        this.title = assessment.getTitle();
        this.status = assessment.getStatus().name();
        this.topicConjunctions = assessment.getTopicConjunctions().stream().map(TopicConjunctionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                ", sequence=" + sequence +
                ", numberOfQuestions=" + numberOfQuestions +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", topicConjunctions=" + topicConjunctions +
                '}';
    }
}