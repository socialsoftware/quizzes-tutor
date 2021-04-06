package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;

import java.util.List;
import java.util.Set;

public interface QuestionContract {
    List<TopicWithCourseDto> findTopics(Set<Integer> topicsList);
}
