package pt.ulisboa.tecnico.socialsoftware.common.activity;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public interface AnswerActivities {

    Integer generateQuiz(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizForm);

    // void deleteQuiz();

}
