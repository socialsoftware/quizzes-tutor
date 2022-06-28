package pt.ulisboa.tecnico.socialsoftware.common.activity;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface AnswerActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TUTOR_TASK_LIST)
    Integer generateQuiz(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizForm);

    // void deleteQuiz();

}
