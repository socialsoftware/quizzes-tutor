package pt.ulisboa.tecnico.socialsoftware.tournament;

import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

@Component
public class TournamentACL {

    public TournamentCreator userToTournamentCreator(UserDto user) {
        return new TournamentCreator(user.getId(), user.getUsername(), user.getName());
    }

    public TournamentCourseExecution courseExecutionToTournamentCourseExecution(CourseExecutionDto courseExecution) {
        return new TournamentCourseExecution(courseExecution.getCourseExecutionId(),
                courseExecution.getCourseId(), CourseExecutionStatus.valueOf(courseExecution.getStatus().toString()), courseExecution.getAcronym());
    }

    public TournamentTopic topicToTournamentTopic(TopicWithCourseDto topic) {
        return new TournamentTopic(topic.getId(), topic.getName(), topic.getCourseId());
    }

    public TournamentParticipant userToTournamentParticipant(UserDto user) {
        return new TournamentParticipant(user.getId(), user.getUsername(), user.getName());
    }
}
