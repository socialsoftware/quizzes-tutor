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
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository.ReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository.SubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private SubmissionRepository submissionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private QuestionService questionService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
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

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReviewDto createReview(ReviewDto reviewDto) {
        checkIfConsistentReview(reviewDto);

        Submission submission = getSubmission(reviewDto.getSubmissionId());

        User user = getTeacher(reviewDto.getUserId());

        updateQuestionStatus(reviewDto.getStatus(), submission.getQuestion().getId());

        Review review = new Review(user, submission, reviewDto);

        entityManager.persist(review);
        return new ReviewDto(review);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto updateSubmission(Integer submissionId, SubmissionDto submissionDto) {
        Submission submission = getSubmission(submissionId);

        this.questionService.updateQuestion(submissionDto.getQuestion().getId(), submissionDto.getQuestion());
        return new SubmissionDto(submission);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void toggleInReviewStatus(int questionId, boolean inReview) {
        String status = inReview ? "IN_REVIEW" : "IN_REVISION";
        updateQuestionStatus(status, questionId);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SubmissionDto> getStudentSubmissions(Integer studentId, Integer courseExecutionId) {
        return submissionRepository.getSubmissions(studentId, courseExecutionId).stream().map(SubmissionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SubmissionDto> getCourseExecutionSubmissions(Integer courseExecutionId) {
        return submissionRepository.getCourseExecutionSubmissions(courseExecutionId).stream().map(SubmissionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReviewDto> getSubmissionReviews(Integer submissionId) {
        return reviewRepository.getSubmissionReviews(submissionId).stream().map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SubmissionDto> getAllStudentsSubmissions(Integer courseExecutionId) {
        List<SubmissionDto> submissions = submissionRepository.getCourseExecutionSubmissions(courseExecutionId).stream()
                .map(SubmissionDto::new).collect(Collectors.toList());

        for (SubmissionDto submission : submissions) {
            if (submission.isAnonymous())
                submission.setName(null);
        }

        return submissions;
    }

    private void checkIfConsistentSubmission(SubmissionDto submissionDto) {
        if (submissionDto.getQuestion() == null)
            throw new TutorException(SUBMISSION_MISSING_QUESTION);
        else if (submissionDto.getUserId() == null)
            throw new TutorException(SUBMISSION_MISSING_STUDENT);
        else if (submissionDto.getCourseExecutionId() == null)
            throw new TutorException(SUBMISSION_MISSING_COURSE);
    }

    private void checkIfConsistentReview(ReviewDto reviewDto) {
        if (reviewDto.getJustification() == null || reviewDto.getJustification().isEmpty())
            throw new TutorException(REVIEW_MISSING_JUSTIFICATION);
        else if (reviewDto.getSubmissionId() == null)
            throw new TutorException(REVIEW_MISSING_SUBMISSION);
        else if (reviewDto.getUserId() == null)
            throw new TutorException(REVIEW_MISSING_TEACHER);
        else if (!Stream.of(Question.Status.values()).map(String::valueOf).collect(Collectors.toList()).contains(reviewDto.getStatus()) ||
                reviewDto.getStatus() == null || reviewDto.getStatus().isBlank())
            throw new TutorException(INVALID_STATUS_FOR_QUESTION);
    }

    private CourseExecution getCourseExecution(Integer executionId) {
        return courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
    }

    private Submission getSubmission(Integer submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
    }

    private Question getQuestion(Integer questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    private User getStudent(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        if (!user.isStudent())
            throw new TutorException(USER_NOT_STUDENT, user.getUsername());
        return user;
    }

    private User getTeacher(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        if (!user.isTeacher())
            throw new TutorException(USER_NOT_TEACHER, user.getUsername());
        return user;
    }

    private Question createQuestion(Course course, QuestionDto questionDto) {
        questionDto.setStatus("IN_REVISION");
        QuestionDto question = questionService.createQuestion(course.getId(), questionDto);
        return questionRepository.findById(question.getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, question.getId()));
    }

    private Submission createSubmission(SubmissionDto submissionDto, CourseExecution courseExecution, Question question, User user) {
        Submission submission = new Submission(courseExecution, question, user);
        submission.setAnonymous(submissionDto.isAnonymous());
        return submission;
    }

    private void updateQuestionStatus(String status, Integer questionId) {
        Question question = getQuestion(questionId);
        if(question.getStatus() == Question.Status.IN_REVISION || question.getStatus() == Question.Status.IN_REVIEW)
            question.setStatus(Question.Status.valueOf(status));
        else
            throw new TutorException(CANNOT_REVIEW_SUBMISSION);
    }
}
