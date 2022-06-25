package pt.ulisboa.tecnico.socialsoftware.tournament.activity.removeTournamentActivities;

public interface RemoveTournamentActivities {

    void beginRemove(Integer tournamentId);

    void undoRemove(Integer tournamentId);

    void confirmRemove(Integer tournamentId);

}
