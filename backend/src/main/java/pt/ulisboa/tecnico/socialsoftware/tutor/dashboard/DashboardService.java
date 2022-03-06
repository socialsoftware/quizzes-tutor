package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_ALREADY_HAS_DASHBOARD;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_NO_COURSE_EXECUTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND;

@Service
public class DashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DashboardDto getDashboard(int courseExecutionId, int studentId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (!student.getCourseExecutions().contains(courseExecution))
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION);

        Optional<Dashboard> dashboardOptional = student.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId() == courseExecutionId)
                .findAny();

        if (dashboardOptional.isPresent()) {
            return new DashboardDto(dashboardOptional.get());
        } else {
            return createAndReturnDashboardDto(courseExecution, student);
        }


    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DashboardDto createDashboard(int courseExecutionId, int studentId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (student.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution() == courseExecution))
            throw new TutorException(STUDENT_ALREADY_HAS_DASHBOARD);

        if (!student.getCourseExecutions().contains(courseExecution))
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION);

        return createAndReturnDashboardDto(courseExecution, student);
    }

    private DashboardDto createAndReturnDashboardDto(CourseExecution courseExecution, Student student) {
        Dashboard dashboard = new Dashboard(courseExecution, student);
        dashboardRepository.save(dashboard);
        return new DashboardDto(dashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        dashboard.remove();
        dashboardRepository.delete(dashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDashboards() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .flatMap(student -> student.getDashboards().stream())
                .forEach(dashboard -> {
                    dashboard.remove();
                    this.dashboardRepository.delete(dashboard);
                });
    }

}
