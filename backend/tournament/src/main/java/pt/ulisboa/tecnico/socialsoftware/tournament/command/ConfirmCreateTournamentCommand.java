package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.Set;

public class ConfirmCreateTournamentCommand implements Command {

    private Integer tournamentId;
    private Integer quizId;
    private TournamentCourseExecution tournamentCourseExecution;
    private Set<TournamentTopic> topics;

    public ConfirmCreateTournamentCommand() {
    }

    public ConfirmCreateTournamentCommand(Integer tournamentId, Integer quizId,
                                          TournamentCourseExecution tournamentCourseExecution,
                                          Set<TournamentTopic> topics) {
        this.tournamentId = tournamentId;
        this.quizId = quizId;
        this.tournamentCourseExecution = tournamentCourseExecution;
        this.topics = topics;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public TournamentCourseExecution getTournamentCourseExecution() {
        return tournamentCourseExecution;
    }

    public void setTournamentCourseExecution(TournamentCourseExecution tournamentCourseExecution) {
        this.tournamentCourseExecution = tournamentCourseExecution;
    }

    public Set<TournamentTopic> getTopics() {
        return topics;
    }

    public void setTopics(Set<TournamentTopic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "ConfirmCreateTournamentCommand{" +
                "tournamentId=" + tournamentId +
                ", quizId=" + quizId +
                ", tournamentCourseExecution=" + tournamentCourseExecution +
                ", topics=" + topics +
                '}';
    }
}
