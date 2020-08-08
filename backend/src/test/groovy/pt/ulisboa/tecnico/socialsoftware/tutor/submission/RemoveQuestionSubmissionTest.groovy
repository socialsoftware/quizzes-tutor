package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class RemoveQuestionSubmissionTest extends SpockTest{
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

    def "teacher removes a submitted question with an associated review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setQuestionSubmission(questionSubmission)
        review.setStatus("IN_REVISION")
        questionSubmission.addReview(review)
        reviewRepository.save(review)

        when:
        questionService.removeQuestion(question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        questionSubmissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "teacher removes a submitted question with 3 associated reviews"(){
        given: "a review"
        def review1 = new Review()
        review1.setComment(REVIEW_1_COMMENT)
        review1.setUser(teacher)
        review1.setQuestionSubmission(questionSubmission)
        review1.setStatus("IN_REVISION")
        questionSubmission.addReview(review1)
        reviewRepository.save(review1)
        and: "another review"
        def review2 = new Review()
        review2.setComment(REVIEW_2_COMMENT)
        review2.setUser(teacher)
        review2.setQuestionSubmission(questionSubmission)
        review2.setStatus("IN_REVISION")
        questionSubmission.addReview(review2)
        reviewRepository.save(review2)
        and: "another review"
        def review3 = new Review()
        review3.setComment(REVIEW_3_COMMENT)
        review3.setUser(teacher)
        review3.setQuestionSubmission(questionSubmission)
        review3.setStatus("IN_REVISION")
        questionSubmission.addReview(review3)
        reviewRepository.save(review3)

        when:
        questionService.removeQuestion(question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        questionSubmissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "student removes a submitted question with no reviews"(){
        given: "questionSubmission is IN_REVISION"
        question.setStatus(Question.Status.IN_REVISION)
        questionRepository.save(question)
        questionSubmission.setQuestion(question)
        questionSubmissionRepository.save(questionSubmission)

        when:
        questionService.removeSubmittedQuestion(questionSubmission.getId(), question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        questionSubmissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "student tries to remove a submitted question already in review"(){
        when:
        questionService.removeSubmittedQuestion(questionSubmission.getId(), question.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_DELETE_REVIEWED_QUESTION
    }

    def "student tries to remove a submitted question with an associated review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setQuestionSubmission(questionSubmission)
        review.setStatus("IN_REVISION")
        questionSubmission.addReview(review)
        reviewRepository.save(review)

        when:
        questionService.removeSubmittedQuestion(questionSubmission.getId(), question.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_DELETE_REVIEWED_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




