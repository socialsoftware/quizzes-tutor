package pt.ulisboa.tecnico.socialsoftware.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.remote.AnswerInterface;
import pt.ulisboa.tecnico.socialsoftware.common.remote.CourseExecutionInterface;

@Service
public class TournamentRequiredService {

    @Autowired
    private CourseExecutionInterface courseExecutionInterface;

    @Autowired
    private AnswerInterface answerInterface;


    public CourseExecutionDto getDemoCourseExecutionId() {
        return courseExecutionInterface.findDemoCourseExecution();
    }

    public StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId) {
        return answerInterface.startQuiz(userId, quizId);
    }

}
