package pt.ulisboa.tecnico.socialsoftware.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentParticipantDto;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

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

    @Column(name = "number_of_answered")
    private Integer numberOfAnswered;

    @Column(name = "number_of_correct")
    private Integer numberOfCorrect;

    public TournamentParticipant() {
    }

    public TournamentParticipant(Integer userId, String username, String name) {
        this.id = userId;
        this.username = username;
        this.name = name;
        this.answered = false;
        this.numberOfAnswered = 0;
        this.numberOfCorrect = 0;
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

    public boolean hasAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Integer getNumberOfAnswered() {
        return numberOfAnswered;
    }

    public void setNumberOfAnswered(Integer numberOfAnswered) {
        this.numberOfAnswered = numberOfAnswered;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentParticipant that = (TournamentParticipant) o;
        return id.equals(that.id) && username.equals(that.username) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name);
    }

    public TournamentParticipantDto getDto() {
        TournamentParticipantDto dto = new TournamentParticipantDto();
        dto.setUserId(getId());
        dto.setName(getName());
        dto.setUsername(getUsername());
        dto.setAnswered(hasAnswered());
        dto.setNumberOfAnswered(getNumberOfAnswered());
        dto.setNumberOfCorrect(getNumberOfCorrect());
        dto.setScore(getNumberOfCorrect() - (getNumberOfAnswered() - getNumberOfCorrect()) * 0.3);
        return dto;
    }

    @Override
    public String toString() {
        return "TournamentParticipant{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", answered=" + answered +
                ", numberOfAnswered=" + numberOfAnswered +
                ", numberOfCorrect=" + numberOfCorrect +
                '}';
    }
}
