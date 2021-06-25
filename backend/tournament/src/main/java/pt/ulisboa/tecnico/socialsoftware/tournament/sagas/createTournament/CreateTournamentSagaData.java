package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.GetCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.question.GetTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmCreateTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.RejectCreateTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.StoreTournamentCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.StoreTournamentTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.TOURNAMENT_MISSING_TOPICS;

public class CreateTournamentSagaData {
    private final Logger logger = LoggerFactory.getLogger(CreateTournamentSagaData.class);

    private Integer tournamentId;
    private Integer quizId;
    private ExternalStatementCreationDto externalStatementCreationDto;
    private Integer creatorId;
    private Integer courseExecutionId;
    private TournamentCourseExecution tournamentCourseExecution;
    private Set<TournamentTopic> topics = new HashSet<>();
    private TopicListDto topicListDto;

    public CreateTournamentSagaData() {
    }

    public CreateTournamentSagaData(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
                                    ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto) {
        this.tournamentId = tournamentId;
        this.creatorId = creatorId;
        this.courseExecutionId = courseExecutionId;
        this.externalStatementCreationDto = externalStatementCreationDto;
        this.topicListDto = topicListDto;
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

    public ExternalStatementCreationDto getExternalStatementCreationDto() {
        return externalStatementCreationDto;
    }

    public void setExternalStatementCreationDto(ExternalStatementCreationDto externalStatementCreationDto) {
        this.externalStatementCreationDto = externalStatementCreationDto;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public TournamentCourseExecution getTournamentCourseExecution() {
        return tournamentCourseExecution;
    }

    public void setTournamentCourseExecution(TournamentCourseExecution tournamentCourseExecution) {
        this.tournamentCourseExecution = tournamentCourseExecution;
    }

    public Set<TournamentTopic> getTournamentTopics() {
        return topics;
    }

    public void setTopics(Set<TournamentTopic> topics) {
        this.topics = topics;
    }

    public TopicListDto getTopicListDto() {
        return topicListDto;
    }

    public void setTopicListDto(TopicListDto topicListDto) {
        this.topicListDto = topicListDto;
    }

    GenerateQuizCommand generateTournamentQuiz() {
        logger.info("Sent GenerateTournamentQuizCommand");
        ExternalStatementCreationDto dto = getExternalStatementCreationDto();
        dto.setTopics(getTournamentTopics().stream().map(TournamentTopic::getTopicDto).collect(Collectors.toSet()));
        return new GenerateQuizCommand(getCreatorId(), getCourseExecutionId(), dto);
    }

    void handleGenerateTournamentQuiz(Integer reply) {
        logger.info("Received handleGenerateTournamentQuiz quizId: " + reply);
        setQuizId(reply);
    }

    RejectCreateTournamentCommand undoCreateTournament() {
        logger.info("Sent RejectCreateTournamentCommand");
        return new RejectCreateTournamentCommand(getTournamentId());
    }

    ConfirmCreateTournamentCommand confirmCreateTournament() {
        logger.info("Sent ConfirmCreateTournamentCommand");
        return new ConfirmCreateTournamentCommand(getTournamentId(), getQuizId());
    }

    void saveTournamentCourseExecution(CourseExecutionDto courseExecutionDto) {
        logger.info("Received saveTournamentCourseExecution courseExecutionDto: " + courseExecutionDto);
        setTournamentCourseExecution(new TournamentCourseExecution(courseExecutionDto.getCourseExecutionId(),
                courseExecutionDto.getCourseId(), CourseExecutionStatus.valueOf(courseExecutionDto.getStatus().toString()),
                courseExecutionDto.getAcronym()));
    }

    GetCourseExecutionCommand getCourseExecution() {
        logger.info("Sent GetCourseExecutionCommand");
        return new GetCourseExecutionCommand(getCourseExecutionId());
    }

    void saveTopics(FindTopicsDto topicWithCourseDtoList) {
        logger.info("Received saveTopics topicWithCourseDtoList: " + topicWithCourseDtoList);

        if (topicWithCourseDtoList.getTopicWithCourseDtoList().isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }
        else {
            for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList.getTopicWithCourseDtoList()) {
                topics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                        topicWithCourseDto.getCourseId()));
            }
        }
    }

    GetTopicsCommand getTopics() {
        logger.info("Sent GetTopicsCommand");
        return new GetTopicsCommand(getTopicListDto());
    }

    @Override
    public String toString() {
        return "CreateTournamentSagaData{" +
                "tournamentId=" + tournamentId +
                ", quizId=" + quizId +
                ", externalStatementCreationDto=" + externalStatementCreationDto +
                ", creatorId=" + creatorId +
                ", courseExecutionId=" + courseExecutionId +
                ", tournamentCourseExecution=" + tournamentCourseExecution +
                ", topics=" + topics +
                ", topicListDto=" + topicListDto +
                '}';
    }

    StoreTournamentTopicsCommand storeTopics() {
        logger.info("Sent StoreTournamentTopicsCommand");
        return new StoreTournamentTopicsCommand(getTournamentId(), getTournamentTopics());
    }

    StoreTournamentCourseExecutionCommand storeCourseExecution() {
        logger.info("Sent StoreTournamentCourseExecutionCommand");
        return new StoreTournamentCourseExecutionCommand(getTournamentId(), getTournamentCourseExecution());
    }
}
