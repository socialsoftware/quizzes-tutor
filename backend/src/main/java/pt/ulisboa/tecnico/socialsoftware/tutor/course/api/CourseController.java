package pt.ulisboa.tecnico.socialsoftware.tutor.course.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TarGZip;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;


@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepository quizRepository;

    @Value("${export.dir}")
    private String exportDir;

    @GetMapping("/courses/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public List<CourseDto> getCourseExecutions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        User.Role role;
        if (user.isAdmin()) {
            role = User.Role.ADMIN;
        } else {
            role = user.getRole();
        }

        return courseService.getCourseExecutions(role);
    }

    @GetMapping("/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public CourseDto getCourseExecutionById(@PathVariable Integer courseExecutionId) {
        return courseService.getCourseExecutionById(courseExecutionId);
    }

    @PostMapping("/courses/activate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'EXECUTION.CREATE')")
    public CourseDto activateCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createTecnicoCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/deactivate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public void deactivateCourseExecution(@PathVariable int executionId) {
        courseService.deactivateCourseExecution(executionId);
    }

    @GetMapping("/executions/{executionId}/anonymize")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public void anonymizeCourseExecutionUsers(@PathVariable int executionId) {
        courseService.anonymizeCourseExecutionUsers(executionId);
    }

    @PostMapping("/courses/external")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseDto, 'DEMO.ACCESS'))")
    public CourseDto createExternalCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createExternalCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/students")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<StudentDto> getCourseStudents(@PathVariable int executionId) {
        return courseService.getCourseStudents(executionId);
    }

    @DeleteMapping("/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseExecutionId, 'DEMO.ACCESS'))")
    public void removeCourseExecution(@PathVariable Integer courseExecutionId) {
        courseService.removeCourseExecution(courseExecutionId);
    }

    @GetMapping("/executions/{executionId}/users/external")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public List<ExternalUserDto> getExternalUsers(@PathVariable Integer executionId) {
        return courseService.getExternalUsers(executionId);
    }

    @PostMapping("/executions/{executionId}/users/delete/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public CourseDto deleteExternalInactiveUsers(@PathVariable Integer executionId, @Valid @RequestBody List<Integer> usersIds) {
        return courseService.deleteExternalInactiveUsers(executionId, usersIds);
    }

    @GetMapping(value = "/executions/{executionId}/export")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS')) or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public void exportCourseExecutionInfo(HttpServletResponse response, @PathVariable Integer executionId) throws IOException {
        List<Quiz> courseExecutionQuizzes = quizRepository.findQuizzesOfExecution(executionId);
        response.setHeader("Content-Disposition", "attachment; filename=file.tar.gz");
        response.setContentType("application/tar.gz");
        String sourceFolder = exportDir + "/quizzes-" + executionId;
        File file = new File(sourceFolder);
        file.mkdir();
        for (Quiz quiz : courseExecutionQuizzes) {
            answerService.writeQuizAnswers(quiz.getId());
            this.quizService.createQuizXmlDirectory(quiz.getId(), sourceFolder);
        }
        TarGZip tGzipDemo = new TarGZip(sourceFolder);
        tGzipDemo.createTarFile();
        response.getOutputStream().write(Files.readAllBytes(Paths.get(sourceFolder + ".tar.gz")));
        response.flushBuffer();

        deleteDirectory(file);
        deleteDirectory(new File(sourceFolder + ".tar.gz"));
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}

