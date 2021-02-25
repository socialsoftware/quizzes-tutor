package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TournamentParticipant {

    @Column(name = "participant_id")
    private Integer id;

    @Column(name = "participant_username")
    private String username;

    @Column(name = "participant_name")
    private String name;

    @Column(name = "participant_answered")
    private boolean answered;

    public TournamentParticipant() {
    }

    public TournamentParticipant(Integer userId, String username, String name, boolean answered) {
        this.id = userId;
        this.username = username;
        this.name = name;
        this.answered = answered;
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

    public boolean isAnswered() {
        return answered;
    }
}
