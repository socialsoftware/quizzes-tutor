package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament;

import java.io.Serializable;

public class TournamentTopicDto implements Serializable {
    private Integer id;
    private String name;

    public TournamentTopicDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
