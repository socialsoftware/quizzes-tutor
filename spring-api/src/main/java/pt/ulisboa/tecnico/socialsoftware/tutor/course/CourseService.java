package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@Service
public class CourseService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 3,
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
      maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(String name) {
        Course course= courseRepository.findByName(name);
        if (course == null) {
            throw new TutorException(COURSE_NOT_FOUND, name);
        }
        return course.getCourseExecutions().stream()
                .map(CourseDto::new)
                .sorted(Comparator.comparing(CourseDto::getAcademicTerm).reversed())
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto createCourseExecution(CourseDto courseDto) {
        Course course = courseRepository.findByName(courseDto.getName());
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
      maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> courseStudents(String name, String acronym, String academicTerm) {
        CourseExecution courseExecution = courseRepository.findByName(name).getCourseExecution(acronym, academicTerm).orElse(null);
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
