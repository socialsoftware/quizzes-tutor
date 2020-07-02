package pt.ulisboa.tecnico.socialsoftware.tutor.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class SubmissionService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto createSubmission(SubmissionDto submissionDto) {
        checkIfConsistentSubmission(submissionDto);

        CourseExecution courseExecution = getCourseExecution(submissionDto.getCourseExecutionId());

        Question question = createQuestion(courseExecution.getCourse(), submissionDto.getQuestion());

        User user = getStudent(submissionDto.getUserId());

        Submission submission = createSubmission(submissionDto, courseExecution, question, user);

        entityManager.persist(submission);
        return new SubmissionDto(submission);
    }

    private void checkIfConsistentSubmission(SubmissionDto submissionDto) {
        if (submissionDto.getQuestion() == null)
            throw new TutorException(SUBMISSION_MISSING_QUESTION);
        else if (submissionDto.getUserId() == null)
            throw new TutorException(SUBMISSION_MISSING_STUDENT);
        else if (submissionDto.getCourseExecutionId() == null)
            throw new TutorException(SUBMISSION_MISSING_COURSE);
    }

    private CourseExecution getCourseExecution(Integer executionId) {
        return courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
    }

    private Question createQuestion(Course course, QuestionDto questionDto) {
        QuestionDto question = questionService.createQuestion(course.getId(), questionDto);
        return questionRepository.findById(question.getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, question.getId()));
    }

    private Submission createSubmission(SubmissionDto submissionDto, CourseExecution courseExecution, Question question, User user) {
        Submission submission = new Submission(courseExecution, question, user);
        submission.setAnonymous(submissionDto.isAnonymous());
        return submission;
    }

    private User getStudent(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        if (!user.isStudent())
            throw new TutorException(USER_NOT_STUDENT, user.getUsername());
        return user;
    }

}
