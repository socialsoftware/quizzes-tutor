package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Shared
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateReviewTest extends SpockTest{
    def student
    def teacher
    def question
    @Shared
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
        reviewDto.setSubmissionId(submission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_1_JUSTIFICATION)
        reviewDto.setStatus('AVAILABLE')

        when:
        submissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_1_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        result.getStatus().equals("AVAILABLE")
        question.getStatus() == Question.Status.AVAILABLE
    }

    def "create review that approves submission (question disabled)"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_1_JUSTIFICATION)
        reviewDto.setStatus('DISABLED')

        when:
        submissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_1_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        result.getStatus().equals("DISABLED")
        question.getStatus() == Question.Status.DISABLED
    }

    def "create review that rejects submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_1_JUSTIFICATION)
        reviewDto.setStatus('REJECTED')

        when:
        submissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_1_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        result.getStatus().equals("REJECTED")
        question.getStatus() == Question.Status.REJECTED
    }

    def "create review to request changes to submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setJustification(REVIEW_1_JUSTIFICATION)
        reviewDto.setStatus('SUBMITTED')

        when:
        submissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getJustification() == REVIEW_1_JUSTIFICATION
        result.getSubmission() == submission
        result.getUser() == teacher
        result.getStatus().equals("SUBMITTED")
        question.getStatus() == Question.Status.SUBMITTED
    }

    def "user is not a teacher"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submission.getId())
        reviewDto.setUserId(student.getId())
        reviewDto.setJustification(REVIEW_1_JUSTIFICATION)
        reviewDto.setStatus('AVAILABLE')

        when:
        submissionService.createReview(reviewDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_TEACHER
    }

    @Unroll
    def "invalid arguments: justification=#justification | submissionId=#submissionId | userId=#userId | status=#status || errorMessage"(){

        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setSubmissionId(submissionId)
        reviewDto.setUserId(userId)
        reviewDto.setJustification(justification)
        reviewDto.setStatus(status)

        when: submissionService.createReview(reviewDto)

        then: "a TutorException is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        justification          | submissionId       | userId                          | status        || errorMessage
        null                   | submission.getId() | submission.getUser().getId()    | 'AVAILABLE'   || REVIEW_MISSING_JUSTIFICATION
        ' '                    | submission.getId() | submission.getUser().getId()    | 'AVAILABLE'   || REVIEW_MISSING_JUSTIFICATION
        REVIEW_1_JUSTIFICATION | null               | submission.getUser().getId()    | 'AVAILABLE'   || REVIEW_MISSING_SUBMISSION
        REVIEW_1_JUSTIFICATION | submission.getId() | null                            | 'AVAILABLE'   || REVIEW_MISSING_TEACHER
        REVIEW_1_JUSTIFICATION | submission.getId() | submission.getUser().getId()    | null          || INVALID_STATUS_FOR_QUESTION
        REVIEW_1_JUSTIFICATION | submission.getId() | submission.getUser().getId()    | ' '           || INVALID_STATUS_FOR_QUESTION
        REVIEW_1_JUSTIFICATION | submission.getId() | submission.getUser().getId()    | 'INVALID'     || INVALID_STATUS_FOR_QUESTION

    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


