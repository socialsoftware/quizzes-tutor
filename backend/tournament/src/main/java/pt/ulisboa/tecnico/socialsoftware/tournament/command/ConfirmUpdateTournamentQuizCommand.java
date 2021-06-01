package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.Set;

public class ConfirmUpdateTournamentQuizCommand implements Command {
    private Integer tournamentId;
    private TournamentDto tournamentDto;
    private Set<TournamentTopic> topics;

    public ConfirmUpdateTournamentQuizCommand() {
    }

    public ConfirmUpdateTournamentQuizCommand(Integer tournamentId, TournamentDto tournamentDto, Set<TournamentTopic> topics) {
        this.tournamentId = tournamentId;
        this.tournamentDto = tournamentDto;
        this.topics = topics;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public TournamentDto getTournamentDto() {
        return tournamentDto;
    }

    public void setTournamentDto(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;
    }

    public Set<TournamentTopic> getTopics() {
        return topics;
    }

    public void setTopics(Set<TournamentTopic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "ConfirmUpdateTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                ", tournamentDto=" + tournamentDto +
                ", topics=" + topics +
                '}';
    }
}
