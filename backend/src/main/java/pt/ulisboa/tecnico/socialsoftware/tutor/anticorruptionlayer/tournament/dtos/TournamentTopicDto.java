package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

import java.io.Serializable;

public class TournamentTopicDto implements Serializable {
    private Integer id;
    private String name;

    public TournamentTopicDto() {
    }

    public TournamentTopicDto(TournamentTopic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
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

    @Override
    public String toString() {
        return "TournamentTopicDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
