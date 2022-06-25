package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.removeTournament;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.common.activity.QuizActivities;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.TournamentActivities;

public class RemoveTournamentWorkflowImpl implements RemoveTournamentWorkflow {

    private final TournamentActivities tournamentActivities = Workflow.newActivityStub(TournamentActivities.class);

    private final QuizActivities quizActivities = Workflow.newActivityStub(QuizActivities.class);

    @Override
    public void removeTournament(Integer tournamentId, Integer quizId) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            tournamentActivities.beginRemove(tournamentId);
            saga.addCompensation(tournamentActivities::undoRemove, tournamentId);

            quizActivities.deleteQuiz(quizId);

            tournamentActivities.confirmRemove(tournamentId);

        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
    }

}
