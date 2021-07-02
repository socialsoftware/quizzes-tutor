package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.Set;

public class UndoUpdateTopicsTournamentCommand implements Command {

    private Integer tournamentId;
    private Set<TournamentTopic> oldTopics;

    public UndoUpdateTopicsTournamentCommand() {
    }

    public UndoUpdateTopicsTournamentCommand(Integer tournamentId, Set<TournamentTopic> oldTopics) {
        this.tournamentId = tournamentId;
        this.oldTopics = oldTopics;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Set<TournamentTopic> getOldTopics() {
        return oldTopics;
    }

    public void setOldTopics(Set<TournamentTopic> oldTopics) {
        this.oldTopics = oldTopics;
    }

    @Override
    public String toString() {
        return "UndoUpdateTopicsTournamentCommand{" +
                "tournamentId=" + tournamentId +
                ", oldTopics=" + oldTopics +
                '}';
    }
}
