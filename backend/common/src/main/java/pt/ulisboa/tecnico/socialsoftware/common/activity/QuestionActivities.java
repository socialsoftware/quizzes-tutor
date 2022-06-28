package pt.ulisboa.tecnico.socialsoftware.common.activity;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface QuestionActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TUTOR_TASK_LIST)
    FindTopicsDto getTopics(TopicListDto topicListDto);

}
