package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.updateTournament;

import java.util.HashSet;
import java.util.Set;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.common.activity.QuestionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.QuizActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities.UpdateTournamentActivities;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.TOURNAMENT_MISSING_TOPICS;

public class UpdateTournamentWorkflowImpl implements UpdateTournamentWorkflow {

    private final UpdateTournamentActivities updateTournamentActivities = Workflow
            .newActivityStub(UpdateTournamentActivities.class);

    private final QuestionActivities questionActivities = Workflow
            .newActivityStub(QuestionActivities.class);

    private final QuizActivities quizActivities = Workflow
            .newActivityStub(QuizActivities.class);

    @Override
    public void updateTournament(Integer tournamentId, TournamentDto newTournamentDto,
            TournamentDto oldTournamentDto,
            TopicListDto topicListDto, Set<TournamentTopic> oldTopics, Integer executionId) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Step 1
            updateTournamentActivities.beginUpdate(tournamentId);
            saga.addCompensation(updateTournamentActivities::undoUpdate, tournamentId);

            // Step 2
            FindTopicsDto topicWithCourseDtoList = questionActivities.getTopics(topicListDto);

            Set<TournamentTopic> newTopics = new HashSet<>();
            if (topicWithCourseDtoList.getTopicWithCourseDtoList().isEmpty()) {
                throw new TutorException(TOURNAMENT_MISSING_TOPICS);
            } else {
                for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList.getTopicWithCourseDtoList()) {
                    TopicDto topic = new TopicDto();
                    topic.setId(topicWithCourseDto.getId());
                    topic.setName(topicWithCourseDto.getName());
                    newTournamentDto.getTopicsDto().add(topic);

                    newTopics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                            topicWithCourseDto.getCourseId()));
                }
            }

            // Step 3
            updateTournamentActivities.updateTopics(tournamentId, newTopics);
            saga.addCompensation(updateTournamentActivities::undoUpdateTopics, tournamentId, oldTopics);

            // Step 4
            quizActivities.updateQuiz(newTournamentDto.getCreator().getId(), executionId, newTournamentDto.getQuizId(),
                    newTournamentDto);
            saga.addCompensation(quizActivities::updateQuiz, newTournamentDto.getCreator().getId(), executionId,
                    newTournamentDto.getQuizId(), oldTournamentDto);

            // Step 5
            updateTournamentActivities.confirmUpdate(tournamentId, newTournamentDto);

        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
    }

}
