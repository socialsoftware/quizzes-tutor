package pt.ulisboa.tecnico.socialsoftware.tutor.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AdministrationService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto createExternalCourseExecution(CourseDto courseDto) {
        checkCourseType(courseDto);

        Course course = getCourse(courseDto);

        if (course == null) {
            course = createCourse(courseDto);
        }

        CourseExecution courseExecution = createCourseExecution(courseDto, course);

        return new CourseDto(courseExecution);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto getCourseExecutionById(int courseExecutionId) {
        return courseExecutionRepository.findById(courseExecutionId)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(User.Role role) {
        return courseExecutionRepository.findAll().stream()
                .filter(courseExecution -> role.equals(User.Role.ADMIN) ||
                        (role.equals(User.Role.DEMO_ADMIN) && courseExecution.getCourse().getName().equals(Demo.COURSE_NAME)))
                .map(CourseDto::new)
                .sorted(Comparator
                        .comparing(CourseDto::getName)
                        .thenComparing(CourseDto::getAcademicTerm))
                .collect(Collectors.toList());

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeCourseExecution(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        courseExecution.delete();

        courseExecutionRepository.delete(courseExecution);
    }

    private CourseExecution createCourseExecution(CourseDto courseDto, Course course) {
        CourseExecution courseExecution = new CourseExecution(course, courseDto.getAcronym(), courseDto.getAcademicTerm(), Course.Type.EXTERNAL);
        courseExecutionRepository.save(courseExecution);
        return courseExecution;
    }

    private Course createCourse(CourseDto courseDto) {
        Course course;
        if (courseDto.getCourseType().equals(Course.Type.EXTERNAL)) {
            course = new Course(courseDto.getName(), Course.Type.EXTERNAL);
            courseRepository.save(course);
        } else {
            throw new TutorException(COURSE_NOT_FOUND, courseDto.getName());
        }
        return course;
    }

    private Course getCourse(CourseDto courseDto) {
        Course course;
        if (courseDto.getCourseType().equals(Course.Type.EXTERNAL)) {
            course = courseRepository.findByNameType(courseDto.getName(), Course.Type.EXTERNAL.name()).orElse(null);
        } else {
            course = courseRepository.findByNameType(courseDto.getName(), Course.Type.TECNICO.name()).orElse(null);
        }
        return course;
    }

    private void checkCourseType(CourseDto courseDto) {
        if (courseDto.getCourseType() == null) {
            throw new TutorException(COURSE_TYPE_NOT_DEFINED);
        }
    }

}
