package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class CreateReviewTest extends SpockTest{
    def student
    def teacher
    def question
    def submission

    def setup() {
        student = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        student.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, User.Role.TEACHER)
        userRepository.save(teacher)
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setCourse(course)
        question.setStatus(Question.Status.IN_REVIEW)
        questionRepository.save(question)
        submission = new Submission()
        submission.setQuestion(question)
        submission.setUser(student)
        submission.setCourseExecution(courseExecution)
        submissionRepository.save(submission)

    }

    def "create review that approves submission (question available)"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('AVAILABLE')

        when:
        submissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        question.getStatus() == Question.Status.AVAILABLE
    }

    def "create review that approves submission (question disabled)"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setStatus('DISABLED')

        when:
        submissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        question.getStatus() == Question.Status.DISABLED
    }

    def "create review that rejects submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('REJECTED')

        when:
        submissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        question.getStatus() == Question.Status.REJECTED
    }

    def "create review to request changes to submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('SUBMITTED')

        when:
        submissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        question.getStatus() == Question.Status.SUBMITTED
    }

    def "invalid question status"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('INVALID')

        when:
        submissionService.createReview(reviewDto)


        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_STATUS_FOR_QUESTION
    }

    def "null question status"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus(null)

        when:
        submissionService.createReview(reviewDto)


        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_STATUS_FOR_QUESTION
    }

    def "create review without justification"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('AVAILABLE')

        when:
        submissionService.createReview(reviewDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.REVIEW_MISSING_JUSTIFICATION
    }

    def "user is not a teacher"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission)
        reviewDto.setUserId(student.getId())
        reviewDto.setJustification(REVIEW_JUSTIFICATION)
        reviewDto.setStatus('AVAILABLE')

        when:
        submissionService.createReview(reviewDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_TEACHER
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


