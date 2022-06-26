package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.removeTournament;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.common.activity.QuizActivities;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.removeTournamentActivities.RemoveTournamentActivities;

public class RemoveTournamentWorkflowImpl implements RemoveTournamentWorkflow {

    private final RemoveTournamentActivities removeTournamentActivities = Workflow
            .newActivityStub(RemoveTournamentActivities.class);

    private final QuizActivities quizActivities = Workflow.newActivityStub(QuizActivities.class);

    @Override
    public void removeTournament(Integer tournamentId, Integer quizId) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            removeTournamentActivities.beginRemove(tournamentId);
            saga.addCompensation(removeTournamentActivities::undoRemove, tournamentId);

            quizActivities.deleteQuiz(quizId);

            removeTournamentActivities.confirmRemove(tournamentId);

        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
    }

}
