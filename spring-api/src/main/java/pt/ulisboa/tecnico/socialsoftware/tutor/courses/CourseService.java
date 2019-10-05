package pt.ulisboa.tecnico.socialsoftware.tutor.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<CourseExecutionDto> findCourseExecutions() {
        return userRepository.getCourseYears().stream()
                .sorted(Comparator.reverseOrder())
                .map(CourseExecutionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<StudentDto> courseStudents(Integer year) {
        return userRepository.courseStudents(year).stream()
                .filter(user -> user.getRole().equals(User.Role.STUDENT.name()))
                .sorted(Comparator.comparing(User::getNumber))
                .map(StudentDto::new)
                .collect(Collectors.toList());
    }



}
