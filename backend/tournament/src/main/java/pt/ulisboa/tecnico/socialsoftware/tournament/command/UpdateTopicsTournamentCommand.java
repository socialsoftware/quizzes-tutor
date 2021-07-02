package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.Set;

public class UpdateTopicsTournamentCommand implements Command {

    private Integer tournamentId;
    private Set<TournamentTopic> tournamentTopics;

    public UpdateTopicsTournamentCommand() {
    }

    public UpdateTopicsTournamentCommand(Integer tournamentId, Set<TournamentTopic> tournamentTopics) {
        this.tournamentId = tournamentId;
        this.tournamentTopics = tournamentTopics;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Set<TournamentTopic> getTournamentTopics() {
        return tournamentTopics;
    }

    public void setTournamentTopics(Set<TournamentTopic> tournamentTopics) {
        this.tournamentTopics = tournamentTopics;
    }

    @Override
    public String toString() {
        return "UpdateTopicsTournamentCommand{" +
                "tournamentId=" + tournamentId +
                ", tournamentTopics=" + tournamentTopics +
                '}';
    }
}
