package pt.ulisboa.tecnico.socialsoftware.tutor.demoutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
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

        CourseExecutionDto dto = new CourseExecutionDto();
        dto.setCourseExecutionType(null);
        dto.setCourseType(null);
        dto.setStatus(null);
        dto.setAcademicTerm("1º Semestre 2020/2021");
        dto.setAcronym("PADI7");
        dto.setName("Desenvolvimento de Aplicações Distribuídas");
        dto.setCourseExecutionId(0);
        dto.setCourseId(0);
        dto.setNumberOfQuestions(0);
        dto.setNumberOfQuizzes(0);
        dto.setNumberOfActiveStudents(0);
        dto.setNumberOfInactiveStudents(0);
        dto.setNumberOfActiveTeachers(0);
        dto.setNumberOfInactiveTeachers(0);
        courseExecutionService.createTecnicoCourseExecution(dto);
    }
}
