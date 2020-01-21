package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::new)
                .sorted(Comparator.comparing(CourseDto::getName))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(String courseName) {
        Course course= courseRepository.findByName(courseName).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseName));

        return course.getCourseExecutions().stream()
                .map(CourseDto::new)
                .sorted(Comparator.comparing(CourseDto::getAcademicTerm).reversed())
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto activateCourseExecution(CourseDto courseDto) {
        Course course = courseRepository.findByName(courseDto.getName()).orElse(null);
        if (course == null) {
            course = new Course(courseDto.getName());
            courseRepository.save(course);
        }
        Course existingCourse = course;
        return existingCourse.getCourseExecution(courseDto.getAcronym(), courseDto.getAcademicTerm())
                .or(() ->  {
                    CourseExecution courseExecution = new CourseExecution(existingCourse, courseDto.getAcronym(), courseDto.getAcademicTerm());
                    courseExecutionRepository.save(courseExecution);
                    return Optional.of(courseExecution);
                })
        .map(CourseDto::new)
        .orElse(null);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> courseStudents(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElse(null);
        if (courseExecution == null) {
            return new ArrayList<>();
        }
        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole().equals(User.Role.STUDENT))
                .sorted(Comparator.comparing(User::getNumber))
                .map(StudentDto::new)
                .collect(Collectors.toList());
    }

}
