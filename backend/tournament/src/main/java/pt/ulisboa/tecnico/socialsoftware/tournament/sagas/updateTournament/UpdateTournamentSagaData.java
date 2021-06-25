package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.question.GetTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.GetQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.UpdateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.*;
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
    private TournamentDto tournamentDto;
    private TopicListDto topicListDto;
    private Integer executionId;

    public UpdateTournamentSagaData() {
    }

    public UpdateTournamentSagaData(Integer tournamentId, TournamentDto tournamentDto, TopicListDto topicListDto,
                                    Set<TournamentTopic> oldTopics, Integer executionId) {
        this.tournamentId = tournamentId;
        this.tournamentDto = tournamentDto;
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

    public TournamentDto getTournamentDto() {
        return tournamentDto;
    }

    public void setTournamentDto(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;
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
        return new UpdateQuizCommand(getTournamentDto().getCreator().getId(), executionId, tournamentDto.getQuizId(),
                getTournamentDto());
    }

    ConfirmUpdateTournamentQuizCommand confirmUpdateTournamentQuiz() {
        logger.info("Sent ConfirmUpdateTournamentQuizCommand");
        return new ConfirmUpdateTournamentQuizCommand(getTournamentId(), getTournamentDto());
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
                tournamentDto.getTopicsDto().add(topic);

                newTopics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                        topicWithCourseDto.getCourseId()));
            }
        }
    }

    GetQuizCommand getQuiz() {
        logger.info("Sent GetQuizCommand");
        return new GetQuizCommand(tournamentDto.getQuizId());
    }

    void saveQuiz(QuizDto quizDto) {
        logger.info("Received saveQuiz quizDto: " + quizDto);
        quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());
        setQuizDto(quizDto);
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
}
