package pt.ulisboa.tecnico.socialsoftware.tutor.answer.activity;

import pt.ulisboa.tecnico.socialsoftware.common.activity.AnswerActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

public class AnswerActivitiesImpl implements AnswerActivities {

    private final AnswerService answerService;

    public AnswerActivitiesImpl(AnswerService answerService) {
        this.answerService = answerService;
    }

    @Override
    public Integer generateQuiz(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizForm) {
        Quiz quiz = answerService.generateExternalQuiz(creatorId, courseExecutionId, quizForm);
        return quiz.getId();
    }

}
