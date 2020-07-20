package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class RemoveSubmittedQuestionTest extends SpockTest{
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

    def "teacher removes a submitted question with an associated review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setSubmission(submission)
        review.setStatus("IN_REVISION")
        submission.addReview(review)
        reviewRepository.save(review)

        when:
        questionService.removeQuestion(teacher.getId(), question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        submissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "teacher removes a submitted question with 3 associated reviews"(){
        given: "a review"
        def review1 = new Review()
        review1.setComment(REVIEW_1_COMMENT)
        review1.setUser(teacher)
        review1.setSubmission(submission)
        review1.setStatus("IN_REVISION")
        submission.addReview(review1)
        reviewRepository.save(review1)
        and: "another review"
        def review2 = new Review()
        review2.setComment(REVIEW_2_COMMENT)
        review2.setUser(teacher)
        review2.setSubmission(submission)
        review2.setStatus("IN_REVISION")
        submission.addReview(review2)
        reviewRepository.save(review2)
        and: "another review"
        def review3 = new Review()
        review3.setComment(REVIEW_3_COMMENT)
        review3.setUser(teacher)
        review3.setSubmission(submission)
        review3.setStatus("IN_REVISION")
        submission.addReview(review3)
        reviewRepository.save(review3)

        when:
        questionService.removeQuestion(teacher.getId(), question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        submissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "student removes a submitted question with no reviews"(){
        when:
        questionService.removeQuestion(student.getId(), question.getId())

        then: "the submitted question and associated reviews are removed"
        questionRepository.count() == 0L
        optionRepository.count() == 0L
        submissionRepository.count() == 0L
        reviewRepository.count() == 0L
    }

    def "student tries to remove a submitted question with an associated review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setSubmission(submission)
        review.setStatus("IN_REVISION")
        submission.addReview(review)
        reviewRepository.save(review)

        when:
        questionService.removeQuestion(student.getId(), question.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_DELETE_REVIEWED_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




