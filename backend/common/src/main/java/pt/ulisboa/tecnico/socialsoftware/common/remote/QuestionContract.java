package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;

import java.util.List;
import java.util.Set;

public interface QuestionContract {
    FindTopicsDto findTopics(TopicListDto topicsList);
}
