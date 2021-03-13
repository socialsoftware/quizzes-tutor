package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament;

import java.io.Serializable;

public class TournamentCreatorDto implements Serializable {
    private Integer id;
    private String username;
    private String name;

    public TournamentCreatorDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
