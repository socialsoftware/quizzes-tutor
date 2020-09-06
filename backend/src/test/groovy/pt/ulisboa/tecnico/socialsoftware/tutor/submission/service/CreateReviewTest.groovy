package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateReviewTest extends SpockTest{
    def student
    def teacher
    def question
    def questionSubmission

    def setup() {
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, false)
        student.setEnrolledCoursesAcronyms(externalCourseExecution.getAcronym())
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.TEACHER, true, false)
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
    def "create review with submission status '#submissionStatus'"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setSubmissionStatus(submissionStatus)

        when:
        questionSubmissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0)
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getQuestionSubmission() == questionSubmission
        result.getUser() == teacher
        question.getStatus() == questionStatus

        where:
        submissionStatus || questionStatus
        'APPROVED'       || Question.Status.AVAILABLE
        'REJECTED'       || Question.Status.SUBMITTED
        'IN_REVIEW'      || Question.Status.SUBMITTED
        'IN_REVISION'    || Question.Status.SUBMITTED
        null             || Question.Status.SUBMITTED
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
        reviewDto.setSubmissionStatus('REJECTED')

        when:
        questionSubmissionService.createReview(reviewDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REVIEW_QUESTION_SUBMISSION
    }

    @Unroll
    def "invalid arguments: comment=#comment | hasQuestionSubmission=#hasQuestionSubmission | hasUser=#hasUser | status=#status || errorMessage"(){
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
        reviewDto.setSubmissionStatus(status)

        when:
        questionSubmissionService.createReview(reviewDto)

        then: "a TutorException is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        comment           | hasQuestionSubmission  | hasUser  | status        || errorMessage
        null              | true                   | true     | 'AVAILABLE'   || REVIEW_MISSING_COMMENT
        ' '               | true                   | true     | 'AVAILABLE'   || REVIEW_MISSING_COMMENT
        REVIEW_1_COMMENT  | false                  | true     | 'AVAILABLE'   || REVIEW_MISSING_QUESTION_SUBMISSION
        REVIEW_1_COMMENT  | true                   | false    | 'AVAILABLE'   || REVIEW_MISSING_USER
        REVIEW_1_COMMENT  | true                   | true     | ' '           || INVALID_STATUS_FOR_QUESTION
        REVIEW_1_COMMENT  | true                   | true     | 'INVALID'     || INVALID_STATUS_FOR_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


