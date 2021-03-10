package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

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

    public TournamentCreatorDto(Integer id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
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
