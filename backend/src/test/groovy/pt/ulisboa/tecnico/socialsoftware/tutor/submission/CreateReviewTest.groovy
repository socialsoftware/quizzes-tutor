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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateReviewTest extends SpockTest{
    def student
    def teacher
    def question
    def questionSubmission

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
        questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setUser(student)
        questionSubmission.setCourseExecution(courseExecution)
        questionSubmissionRepository.save(questionSubmission)
    }

    def "create review that approves question submission (question available)"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus('AVAILABLE')

        when:
        questionSubmissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getQuestionSubmission() == questionSubmission
        result.getUser() == teacher
        result.getStatus() == Review.Status.AVAILABLE
        question.getStatus() == Question.Status.AVAILABLE
    }

    def "create review that approves question submission (question disabled)"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus('DISABLED')

        when:
        questionSubmissionService.createReview(reviewDto)

        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getQuestionSubmission() == questionSubmission
        result.getUser() == teacher
        result.getStatus() == Review.Status.DISABLED
        question.getStatus() == Question.Status.DISABLED
    }

    def "create review that rejects question submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus('REJECTED')

        when:
        questionSubmissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getQuestionSubmission() == questionSubmission
        result.getUser() == teacher
        result.getStatus() == Review.Status.REJECTED
        question.getStatus() == Question.Status.REJECTED
    }

    def "create review to request changes to question submission"() {
        given: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus('IN_REVISION')

        when:
        questionSubmissionService.createReview(reviewDto)


        then:
        def result = reviewRepository.findAll().get(0)
        def question = questionRepository.findAll().get(0);
        result.getId() != null
        result.getComment() == REVIEW_1_COMMENT
        result.getQuestionSubmission() == questionSubmission
        result.getUser() == teacher
        result.getStatus() == Review.Status.IN_REVISION
        question.getStatus() == Question.Status.IN_REVISION
    }

    def "create review for question submission that has already been reviewed"() {
        given: "a question submission that has already been reviewed"
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        and: "a reviewDto"
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionSubmissionId(questionSubmission.getId())
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setStatus('REJECTED')

        when:
        questionSubmissionService.createReview(reviewDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REVIEW_QUESTION_SUBMISSION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


