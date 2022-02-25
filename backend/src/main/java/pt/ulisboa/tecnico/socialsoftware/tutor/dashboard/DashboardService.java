package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_ALREADY_HAS_DASHBOARD;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_NO_COURSE_EXECUTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND;

public class DashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Retryable(
            value = { SQLException.class }, 
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DashboardDto createDashboard(Integer courseExecutionId, Integer userId) {
        if (courseExecutionId == null)
            throw new TutorException(COURSE_EXECUTION_NOT_FOUND);
        if (userId == null)
            throw new TutorException(USER_NOT_FOUND);

        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (student.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution() == courseExecution))
            throw new TutorException(STUDENT_ALREADY_HAS_DASHBOARD);

        if (!student.getCourseExecutions().contains(courseExecution))
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION);

        Dashboard dashboard = new Dashboard(courseExecution, student);
        dashboardRepository.save(dashboard);
        return new DashboardDto(dashboard);
    }

    @Retryable(
            value = { SQLException.class }, 
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        dashboard.remove();
        dashboardRepository.delete(dashboard);
    }
}
