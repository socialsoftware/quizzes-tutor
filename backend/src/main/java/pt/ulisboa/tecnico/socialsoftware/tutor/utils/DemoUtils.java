package pt.ulisboa.tecnico.socialsoftware.tutor.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.QuestionSubmissionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@Component
public class DemoUtils {
    public static final String COURSE_NAME = "Demo Course";
    public static final String COURSE_ACRONYM = "DemoCourse";
    public static final String COURSE_ACADEMIC_TERM = "1st Semester";
    public static final String STUDENT_USERNAME = "demo-student";
    public static final String TEACHER_USERNAME = "demo-teacher";
    public static final String ADMIN_USERNAME = "demo-admin";

    @Autowired
    private UserService userService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionSubmissionService questionSubmissionService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private TournamentService tournamentService;

    public void resetDemoInfo() {
        assessmentService.resetDemoAssessments();
        topicService.resetDemoTopics();
        discussionService.resetDemoDiscussions();
        answerService.resetDemoAnswers();
        tournamentService.resetDemoTournaments();
        quizService.resetDemoQuizzes();
        questionSubmissionService.resetDemoQuestionSubmissions();
        userService.resetDemoStudents();
    }
}
