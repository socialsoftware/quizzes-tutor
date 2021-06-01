package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ExternalStatementCreationDto implements Serializable {
    private Integer id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endTime;
    private Integer numberOfQuestions;
    private Set<TopicDto> topics = new HashSet<>();

    public ExternalStatementCreationDto() {}

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Set<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicDto> topics) {
        this.topics = topics;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ExternalStatementCreationDto{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", numberOfQuestions=" + numberOfQuestions +
                ", topics=" + topics +
                '}';
    }
}
