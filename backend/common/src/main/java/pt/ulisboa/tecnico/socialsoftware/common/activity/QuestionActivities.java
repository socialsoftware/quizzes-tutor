package pt.ulisboa.tecnico.socialsoftware.common.activity;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;

public interface QuestionActivities {

    FindTopicsDto getTopics(TopicListDto topicListDto);

}
