package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateReviewTest extends SpockTest {
    def student
    def teacher
    def question
    def questionSubmission

    def setup() {
        createExternalCourseAndExecution()

        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setCourse(externalCourse)
        question.setStatus(Question.Status.SUBMITTED)
        questionRepository.save(question)
        questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setStatus(QuestionSubmission.Status.IN_REVIEW)
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmissionRepository.save(questionSubmission)
    }

    @Unroll
    def "create review with type '#type'"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setType(type)

        when:
        questionSubmissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0)
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getUser() == teacher
        result.getQuestionSubmission() == questionSubmission
        result.getType().name() == type
        result.getQuestionSubmission().getStatus() == submissionStatus
        question.getStatus() == questionStatus

        where:
        type                               || submissionStatus                       || questionStatus
        Review.Type.APPROVE.name()         || QuestionSubmission.Status.APPROVED     || Question.Status.AVAILABLE
        Review.Type.REJECT.name()          || QuestionSubmission.Status.REJECTED     || Question.Status.SUBMITTED
        Review.Type.REQUEST_CHANGES.name() || QuestionSubmission.Status.IN_REVISION  || Question.Status.SUBMITTED
        Review.Type.REQUEST_REVIEW.name()  || QuestionSubmission.Status.IN_REVIEW    || Question.Status.SUBMITTED
        Review.Type.COMMENT.name()         || QuestionSubmission.Status.IN_REVIEW    || Question.Status.SUBMITTED
    }

    def "create review for question submission that has already been reviewed"() {
        given: "a question submission that has already been reviewed"
        question.setStatus(Question.Status.AVAILABLE)
        questionSubmission.setStatus(QuestionSubmission.Status.APPROVED)

        and: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setType(Review.Type.REJECT.name())

        when:
        questionSubmissionService.createReview(reviewDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REVIEW_QUESTION_SUBMISSION
    }

    @Unroll
    def "invalid arguments: comment=#comment | hasQuestionSubmission=#hasQuestionSubmission | hasUser=#hasUser | type=#type || errorMessage"(){
        given: "a questionSubmission"
        def submission = new QuestionSubmission()
        submission.setQuestion(question)
        submission.setSubmitter(student)
        submission.setCourseExecution(externalCourseExecution)
        submission.setStatus(QuestionSubmission.Status.IN_REVIEW)
        questionSubmissionRepository.save(submission)
        and: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(hasQuestionSubmission ? submission.getId() : null)
        reviewDto.setUserId(hasUser ? submission.getSubmitter().getId() : null)
        reviewDto.setComment(comment)
        reviewDto.setType(type)

        when:
        questionSubmissionService.createReview(reviewDto)

        then: "a TutorException is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        comment           | hasQuestionSubmission  | hasUser  | type                       || errorMessage
        null              | true                   | true     | Review.Type.APPROVE.name() || REVIEW_MISSING_COMMENT
        ' '               | true                   | true     | Review.Type.APPROVE.name() || REVIEW_MISSING_COMMENT
        REVIEW_1_COMMENT  | false                  | true     | Review.Type.APPROVE.name() || REVIEW_MISSING_QUESTION_SUBMISSION
        REVIEW_1_COMMENT  | true                   | false    | Review.Type.APPROVE.name() || REVIEW_MISSING_USER
        REVIEW_1_COMMENT  | true                   | true     | null                       || INVALID_TYPE_FOR_REVIEW
        REVIEW_1_COMMENT  | true                   | true     | 'INVALID'                  || INVALID_TYPE_FOR_REVIEW
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


