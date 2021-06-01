package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.UpdateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.BeginUpdateTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmUpdateTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UndoUpdateTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.Set;

public class UpdateTournamentSagaData {
    private final Logger logger = LoggerFactory.getLogger(UpdateTournamentSagaData.class);

    private Integer tournamentId;
    private QuizDto quizDto;
    private Set<TournamentTopic> topics;
    private TournamentDto tournamentDto;

    public UpdateTournamentSagaData() {
    }

    public UpdateTournamentSagaData(Integer tournamentId, QuizDto quizDto, Set<TournamentTopic> topics,
                                    TournamentDto tournamentDto) {
        this.tournamentId = tournamentId;
        this.quizDto = quizDto;
        this.topics = topics;
        this.tournamentDto = tournamentDto;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public QuizDto getQuizDto() {
        return quizDto;
    }

    public void setQuizDto(QuizDto quizDto) {
        this.quizDto = quizDto;
    }

    public Set<TournamentTopic> getTopics() {
        return topics;
    }

    public void setTopics(Set<TournamentTopic> topics) {
        this.topics = topics;
    }

    public TournamentDto getTournamentDto() {
        return tournamentDto;
    }

    public void setTournamentDto(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;
    }

    BeginUpdateTournamentQuizCommand beginUpdateTournamentQuiz() {
        logger.info("Sent BeginUpdateTournamentQuizCommand");
        return new BeginUpdateTournamentQuizCommand(getTournamentId());
    }

    UndoUpdateTournamentQuizCommand undoUpdateTournamentQuiz() {
        logger.info("Sent UndoUpdateTournamentQuizCommand");
        return new UndoUpdateTournamentQuizCommand(getTournamentId());
    }

    UpdateQuizCommand updateQuiz() {
        logger.info("Sent UpdateQuizCommand");
        return new UpdateQuizCommand(getQuizDto());
    }

    ConfirmUpdateTournamentQuizCommand confirmUpdateTournamentQuiz() {
        logger.info("Sent ConfirmUpdateTournamentQuizCommand");
        return new ConfirmUpdateTournamentQuizCommand(getTournamentId(), getTournamentDto(), getTopics());
    }
}
