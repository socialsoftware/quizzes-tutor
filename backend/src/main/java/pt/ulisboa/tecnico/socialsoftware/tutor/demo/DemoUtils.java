package pt.ulisboa.tecnico.socialsoftware.tutor.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoUtils {
    public static final String COURSE_NAME = "Demo Course";

    public static final String COURSE_ACRONYM = "DemoCourse";

    public static final String COURSE_ACADEMIC_TERM = "1st Semester";

    public static final String STUDENT_USERNAME = "demo-student";

    public static final String TEACHER_USERNAME = "demo-teacher";

    public static final String ADMIN_USERNAME = "demo-admin";

    @Autowired
    private DemoService demoService;

    public void resetDemoInfo() {
        demoService.resetDemoDashboards();
        demoService.resetDemoAssessments();
        demoService.resetDemoTopics();
        demoService.resetDemoDiscussions();
        demoService.resetDemoAnswers();
        demoService.resetDemoTournaments();
        demoService.resetDemoQuizzes();
        demoService.resetDemoQuestionSubmissions();
        demoService.resetDemoStudents();

        demoService.populateDemo();
    }
}
