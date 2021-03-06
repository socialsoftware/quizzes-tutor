package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament;

import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

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
