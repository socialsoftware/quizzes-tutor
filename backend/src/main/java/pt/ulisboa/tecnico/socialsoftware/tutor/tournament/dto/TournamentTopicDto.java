package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

import java.io.Serializable;

public class TournamentTopicDto implements Serializable {
    private Integer id;
    private String name;
    //private Integer numberOfQuestions;

    public TournamentTopicDto() {
    }

    public TournamentTopicDto(TournamentTopic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        //this.numberOfQuestions = topic.getQuestions().size();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }*/

    @Override
    public String toString() {
        return "TopicDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
