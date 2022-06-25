package pt.ulisboa.tecnico.socialsoftware.tutor.question.activity;

import pt.ulisboa.tecnico.socialsoftware.common.activity.QuestionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;

public class QuestionActivitiesImpl implements QuestionActivities {

    private final TopicService topicService;

    public QuestionActivitiesImpl(TopicService topicService) {
        this.topicService = topicService;
    }

    @Override
    public FindTopicsDto getTopics(TopicListDto topicListDto) {
        return topicService.findTopicById(topicListDto.getTopicList());
    }

}
