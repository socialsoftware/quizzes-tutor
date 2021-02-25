package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;

import java.io.Serializable;

public class TournamentCreatorDto implements Serializable {
    private Integer id;
    private String username;
    private String name;

    public TournamentCreatorDto() {
    }

    public TournamentCreatorDto(TournamentCreator creator) {
        this.id = creator.getId();
        this.username = creator.getUsername();
        this.name = creator.getName();
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
