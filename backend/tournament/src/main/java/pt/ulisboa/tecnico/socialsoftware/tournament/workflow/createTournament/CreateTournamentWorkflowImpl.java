package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.common.activity.AnswerActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.QuestionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.QuizActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.activity.CourseExecutionActivities;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.createTournamentActivities.CreateTournamentActivities;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public class CreateTournamentWorkflowImpl implements CreateTournamentWorkflow {

    private static final ErrorMessage TOURNAMENT_MISSING_TOPICS = null;

    private final CreateTournamentActivities createTournamentActivities = Workflow
            .newActivityStub(CreateTournamentActivities.class);

    private final CourseExecutionActivities courseExecutionActivities = Workflow
            .newActivityStub(CourseExecutionActivities.class);

    private final QuestionActivities questionActivities = Workflow.newActivityStub(QuestionActivities.class);

    private final AnswerActivities answerActivities = Workflow.newActivityStub(AnswerActivities.class);

    private final QuizActivities quizActivities = Workflow.newActivityStub(QuizActivities.class);

    @Override
    public Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
            ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Step 1
            saga.addCompensation(createTournamentActivities::undoCreate, tournamentId);

            // Step 2
            CourseExecutionDto courseExecutionDto = courseExecutionActivities.getCourseExecution(courseExecutionId);

            TournamentCourseExecution tournamentCourseExecution = new TournamentCourseExecution(
                    courseExecutionDto.getCourseExecutionId(),
                    courseExecutionDto.getCourseId(),
                    CourseExecutionStatus.valueOf(courseExecutionDto.getStatus().toString()),
                    courseExecutionDto.getAcronym());

            // Step 3
            createTournamentActivities.storeCourseExecution(tournamentId, tournamentCourseExecution);

            // Step 4
            FindTopicsDto topicWithCourseDtoList = questionActivities.getTopics(topicListDto);

            Set<TournamentTopic> tournamentTopics = new HashSet<>();
            if (topicWithCourseDtoList.getTopicWithCourseDtoList().isEmpty()) {
                throw new TutorException(TOURNAMENT_MISSING_TOPICS);
            } else {
                for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList.getTopicWithCourseDtoList()) {
                    tournamentTopics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                            topicWithCourseDto.getCourseId()));
                }
            }

            // Step 5
            createTournamentActivities.storeTopics(tournamentId, tournamentTopics);

            // Step 6
            externalStatementCreationDto.setTopics(
                    tournamentTopics.stream().map(TournamentTopic::getTopicDto).collect(Collectors.toSet()));

            Integer quizId = answerActivities.generateQuiz(creatorId, courseExecutionId,
                    externalStatementCreationDto);

            saga.addCompensation(quizActivities::deleteQuiz, quizId);

            // Step 7
            createTournamentActivities.confirmCreate(tournamentId, quizId);

        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
        return tournamentId;
    }

}
