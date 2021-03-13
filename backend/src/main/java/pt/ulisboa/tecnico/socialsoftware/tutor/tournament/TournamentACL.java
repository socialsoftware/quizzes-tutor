package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

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
