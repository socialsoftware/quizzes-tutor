package pt.ulisboa.tecnico.socialsoftware.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentCreatorDto;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TournamentCreator {

    @Column(name = "creator_id")
    private Integer id;

    @Column(name = "creator_username")
    private String username;

    @Column(name = "creator_name")
    private String name;

    public TournamentCreator() {
    }

    public TournamentCreator(int id, String username, String name) {
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

    public TournamentCreatorDto getDto() {
        TournamentCreatorDto dto = new TournamentCreatorDto();
        dto.setId(getId());
        dto.setName(getName());
        dto.setUsername(getUsername());
        return dto;
    }
}
