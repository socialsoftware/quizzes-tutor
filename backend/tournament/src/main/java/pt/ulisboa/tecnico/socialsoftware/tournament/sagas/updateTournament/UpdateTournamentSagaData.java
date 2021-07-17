package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.question.GetTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.UpdateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmUpdateTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UndoUpdateTopicsTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UndoUpdateTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UpdateTopicsTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.TOURNAMENT_MISSING_TOPICS;

public class UpdateTournamentSagaData {
    private final Logger logger = LoggerFactory.getLogger(UpdateTournamentSagaData.class);

    private Integer tournamentId;
    private QuizDto quizDto;
    private Set<TournamentTopic> newTopics = new HashSet<>();
    private Set<TournamentTopic> oldTopics = new HashSet<>();
    private TournamentDto newTournamentDto;
    private TournamentDto oldTournamentDto;
    private TopicListDto topicListDto;
    private Integer executionId;

    public UpdateTournamentSagaData() {
    }

    public UpdateTournamentSagaData(Integer tournamentId, TournamentDto newTournamentDto, TournamentDto oldTournamentDto,
                                    TopicListDto topicListDto, Set<TournamentTopic> oldTopics, Integer executionId) {
        this.tournamentId = tournamentId;
        this.newTournamentDto = newTournamentDto;
        this.oldTournamentDto = oldTournamentDto;
        this.topicListDto = topicListDto;
        this.oldTopics = oldTopics;
        this.executionId = executionId;
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

    public Set<TournamentTopic> getTournamentTopics() {
        return newTopics;
    }

    public void setNewTopics(Set<TournamentTopic> newTopics) {
        this.newTopics = newTopics;
    }

    public TournamentDto getNewTournamentDto() {
        return newTournamentDto;
    }

    public void setNewTournamentDto(TournamentDto newTournamentDto) {
        this.newTournamentDto = newTournamentDto;
    }

    public TopicListDto getTopicListDto() {
        return topicListDto;
    }

    public void setTopicListDto(TopicListDto topicListDto) {
        this.topicListDto = topicListDto;
    }

    public Set<TournamentTopic> getOldTopics() {
        return oldTopics;
    }

    public void setOldTopics(Set<TournamentTopic> oldTopics) {
        this.oldTopics = oldTopics;
    }

    public Integer getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Integer executionId) {
        this.executionId = executionId;
    }

    public TournamentDto getOldTournamentDto() {
        return oldTournamentDto;
    }

    public void setOldTournamentDto(TournamentDto oldTournamentDto) {
        this.oldTournamentDto = oldTournamentDto;
    }

    UpdateQuizCommand updateQuiz() {
        logger.info("Sent UpdateQuizCommand");
        return new UpdateQuizCommand(getNewTournamentDto().getCreator().getId(), executionId, newTournamentDto.getQuizId(),
                getNewTournamentDto());
    }

    ConfirmUpdateTournamentQuizCommand confirmUpdateTournamentQuiz() {
        logger.info("Sent ConfirmUpdateTournamentQuizCommand");
        return new ConfirmUpdateTournamentQuizCommand(getTournamentId(), getNewTournamentDto());
    }

    GetTopicsCommand getNewTopics() {
        logger.info("Sent GetTopicsCommand");
        return new GetTopicsCommand(getTopicListDto());
    }

    void saveTopics(FindTopicsDto topicWithCourseDtoList) {
        logger.info("Received saveTopics topicWithCourseDtoList: " + topicWithCourseDtoList);

        if (topicWithCourseDtoList.getTopicWithCourseDtoList().isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }
        else {
            for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList.getTopicWithCourseDtoList()) {
                TopicDto topic = new TopicDto();
                topic.setId(topicWithCourseDto.getId());
                topic.setName(topicWithCourseDto.getName());
                newTournamentDto.getTopicsDto().add(topic);

                newTopics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                        topicWithCourseDto.getCourseId()));
            }
        }
    }

    UndoUpdateTournamentCommand undoUpdateTournament() {
        logger.info("Sent UndoUpdateTournamentCommand");
        return new UndoUpdateTournamentCommand(getTournamentId());
    }

    UpdateTopicsTournamentCommand updateTopics() {
        logger.info("Sent UpdateTopicsTournamentCommand");
        return new UpdateTopicsTournamentCommand(getTournamentId(), getTournamentTopics());
    }

    UndoUpdateTopicsTournamentCommand undoUpdateTopics() {
        logger.info("Sent UndoUpdateTopicsTournamentCommand");
        return new UndoUpdateTopicsTournamentCommand(getTournamentId(), getOldTopics());
    }

    UpdateQuizCommand undoUpdateQuiz() {
        logger.info("Sent UpdateQuizCommand");
        return new UpdateQuizCommand(getNewTournamentDto().getCreator().getId(), executionId,
                getNewTournamentDto().getQuizId(), getOldTournamentDto());
    }
}
