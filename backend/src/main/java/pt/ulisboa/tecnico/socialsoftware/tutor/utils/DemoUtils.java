package pt.ulisboa.tecnico.socialsoftware.tutor.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.QuestionSubmissionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@Component
public class DemoUtils {
    @Autowired
    private UserService userService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuestionSubmissionService questionSubmissionService;

    @Autowired
    private TournamentService tournamentService;
    public void resetDemoInfo() {
        tournamentService.resetDemoTournaments();
        quizService.resetDemoQuizzes();
        questionSubmissionService.resetDemoQuestionSubmissions();
        topicService.resetDemoTopics();
        assessmentService.resetDemoAssessments();
        userService.resetDemoStudents();
    }
}
