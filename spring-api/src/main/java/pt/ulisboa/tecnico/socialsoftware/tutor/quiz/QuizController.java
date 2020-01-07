package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'ACCESS'))")
    @PostMapping("/quizzes/non-generated")
    public List<QuizDto> findCourseExecutionNonGeneratedQuizzes(@Valid @RequestBody CourseDto courseDto) {
        return quizService.findCourseExecutionNonGeneratedQuizzes(courseDto);
    }

    @PostMapping("/courses/{name}/executions/{acronym}/{academicTerm}/quizzes")
    public QuizDto createQuiz(@PathVariable String name, @PathVariable String acronym, @PathVariable String academicTerm, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.createQuiz(acronym, academicTerm.replace("_", "/"), quiz);
    }

    @GetMapping("/quizzes/{quizId}")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PutMapping("/quizzes/{quizId}")
    public QuizDto updateQuiz(@PathVariable Integer quizId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.updateQuiz(quizId, quiz);
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity deleteQuiz(@PathVariable Integer quizId) {
        quizService.removeQuiz(quizId);

        return ResponseEntity.ok().build();
    }
}