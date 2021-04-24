package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.COURSE_NOT_FOUND;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<CourseExecutionDto> findCourseWithCourseExecutionsById(int courseId) {
        return courseRepository.findCourseWithCourseExecutionsById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId))
                .getCourseExecutions().stream().map(CourseExecution::getDto).collect(Collectors.toList());
    }
}
