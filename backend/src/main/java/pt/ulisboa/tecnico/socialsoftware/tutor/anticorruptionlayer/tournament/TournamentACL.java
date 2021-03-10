package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament;

import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.execution.dtos.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.user.dtos.UserDto;

@Component
public class TournamentACL {

    public TournamentCreatorDto userToTournamentCreator(UserDto user) {
        return new TournamentCreatorDto(user.getId(), user.getUsername(), user.getName());
    }

    public TournamentCourseExecutionDto courseExecutionToTournamentCourseExecution(CourseExecutionDto courseExecution) {
        return new TournamentCourseExecutionDto(courseExecution.getCourseExecutionId(),
                courseExecution.getCourseId(), CourseExecutionStatus.valueOf(courseExecution.getStatus().toString()), courseExecution.getAcronym());
    }

    public TournamentTopicWithCourseDto topicToTournamentTopic(TopicWithCourseDto topic) {
        return new TournamentTopicWithCourseDto(topic.getId(), topic.getName(), topic.getCourseId());
    }

    public TournamentParticipantDto userToTournamentParticipant(UserDto user) {
        return new TournamentParticipantDto(user.getId(), user.getName(), user.getUsername());
    }
}
