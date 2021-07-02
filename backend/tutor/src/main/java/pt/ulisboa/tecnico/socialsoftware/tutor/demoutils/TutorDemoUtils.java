package pt.ulisboa.tecnico.socialsoftware.tutor.demoutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.QuestionSubmissionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@Component
public class TutorDemoUtils {

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
    private CourseExecutionService courseExecutionService;


    public void resetDemoInfo() {
        assessmentService.resetDemoAssessments();
        topicService.resetDemoTopics();
        discussionService.resetDemoDiscussions();
        answerService.resetDemoAnswers();
        quizService.resetDemoQuizzes();
        questionSubmissionService.resetDemoQuestionSubmissions();
        userService.resetDemoStudents();
    }
}
