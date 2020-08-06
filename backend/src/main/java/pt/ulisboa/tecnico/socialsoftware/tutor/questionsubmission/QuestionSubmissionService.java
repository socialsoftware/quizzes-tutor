package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission;

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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.UserQuestionSubmissionInfoDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.ReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuestionSubmissionService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionSubmissionRepository submissionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private QuestionService questionService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSubmissionDto createQuestionSubmission(QuestionSubmissionDto submissionDto) {
        checkIfConsistentQuestionSubmission(submissionDto);

        CourseExecution courseExecution = getCourseExecution(submissionDto.getCourseExecutionId());

        Question question = createQuestion(courseExecution.getCourse(), submissionDto.getQuestion());

        User user = getStudent(submissionDto.getUserId());

        QuestionSubmission submission = new QuestionSubmission(courseExecution, question, user);

        entityManager.persist(submission);
        return new QuestionSubmissionDto(submission);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReviewDto createReview(ReviewDto reviewDto) {
        checkIfConsistentReview(reviewDto);

        QuestionSubmission submission = getQuestionSubmission(reviewDto.getQuestionSubmissionId());

        User user = userRepository.findById(reviewDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, reviewDto.getUserId()));

        if (!reviewDto.getStatus().equals("COMMENT")) {
            updateQuestionStatus(reviewDto.getStatus(), submission.getQuestion().getId());
        }

        Review review = new Review(user, submission, reviewDto);

        entityManager.persist(review);
        return new ReviewDto(review);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSubmissionDto updateQuestionSubmission(Integer submissionId, QuestionSubmissionDto submissionDto) {
        QuestionSubmission submission = getQuestionSubmission(submissionId);

        User user = userRepository.findById(submissionDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, submissionDto.getUserId()));

        if(user.isStudent() && submission.getQuestion().getStatus() != Question.Status.IN_REVISION) {
            throw new TutorException(CANNOT_EDIT_REVIEWED_QUESTION);
        }

        this.questionService.updateQuestion(submissionDto.getQuestion().getId(), submissionDto.getQuestion());
        return new QuestionSubmissionDto(submission);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void toggleInReviewStatus(int questionId, boolean inReview) {
        String status = inReview ? "IN_REVIEW" : "IN_REVISION";
        updateQuestionStatus(status, questionId);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionSubmissionDto> getStudentQuestionSubmissions(Integer studentId, Integer courseExecutionId) {
        return submissionRepository.getQuestionSubmissions(studentId, courseExecutionId).stream().map(QuestionSubmissionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionSubmissionDto> getCourseExecutionQuestionSubmissions(Integer courseExecutionId) {
        return submissionRepository.getCourseExecutionQuestionSubmissions(courseExecutionId).stream().map(QuestionSubmissionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReviewDto> getQuestionSubmissionReviews(Integer submissionId) {
        return reviewRepository.getQuestionSubmissionReviews(submissionId).stream().map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<UserQuestionSubmissionInfoDto> getAllStudentsQuestionSubmissionsInfo(Integer courseExecutionId) {
        Map<Integer, UserQuestionSubmissionInfoDto> userQuestionSubmissionInfoDtos = new HashMap<>();
        List<QuestionSubmission> submissions = submissionRepository.getCourseExecutionQuestionSubmissions(courseExecutionId);

        for (QuestionSubmission submission: submissions) {
            User user = submission.getUser();
            if (userQuestionSubmissionInfoDtos.containsKey(user.getId())) {
                userQuestionSubmissionInfoDtos.get(user.getId()).addQuestionSubmission();
            } else {
                userQuestionSubmissionInfoDtos.put(user.getId(), new UserQuestionSubmissionInfoDto(user, 1));
            }
        }

        return new ArrayList<>(userQuestionSubmissionInfoDtos.values());
    }

    private void checkIfConsistentQuestionSubmission(QuestionSubmissionDto submissionDto) {
        if (submissionDto.getQuestion() == null)
            throw new TutorException(QUESTION_SUBMISSION_MISSING_QUESTION);
        else if (submissionDto.getUserId() == null)
            throw new TutorException(QUESTION_SUBMISSION_MISSING_STUDENT);
        else if (submissionDto.getCourseExecutionId() == null)
            throw new TutorException(QUESTION_SUBMISSION_MISSING_COURSE);
    }

    private void checkIfConsistentReview(ReviewDto reviewDto) {
        if (reviewDto.getComment() == null || reviewDto.getComment().isEmpty())
            throw new TutorException(REVIEW_MISSING_COMMENT);
        else if (reviewDto.getQuestionSubmissionId() == null)
            throw new TutorException(REVIEW_MISSING_QUESTION_SUBMISSION);
        else if (reviewDto.getUserId() == null)
            throw new TutorException(REVIEW_MISSING_TEACHER);
        else if (reviewDto.getStatus() == null
                || reviewDto.getStatus().isBlank()
                || !Stream.of(Question.Status.values()).map(String::valueOf).collect(Collectors.toList()).contains(reviewDto.getStatus()) && !reviewDto.getStatus().equals("COMMENT")
        )
            throw new TutorException(INVALID_STATUS_FOR_QUESTION);
    }

    private CourseExecution getCourseExecution(Integer executionId) {
        return courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
    }

    private QuestionSubmission getQuestionSubmission(Integer submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new TutorException(QUESTION_SUBMISSION_NOT_FOUND, submissionId));
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

    private Question createQuestion(Course course, QuestionDto questionDto) {
        questionDto.setStatus("IN_REVISION");
        QuestionDto question = questionService.createQuestion(course.getId(), questionDto);
        return questionRepository.findById(question.getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, question.getId()));
    }

    private void updateQuestionStatus(String status, Integer questionId) {
        Question question = getQuestion(questionId);
        if(question.getStatus() == Question.Status.IN_REVISION || question.getStatus() == Question.Status.IN_REVIEW)
            question.setStatus(Question.Status.valueOf(status));
        else
            throw new TutorException(CANNOT_REVIEW_QUESTION_SUBMISSION);
    }
}
