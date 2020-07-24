package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class GetSubmissionReviewsTest extends SpockTest{
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

    def "get submission reviews with 1 review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setSubmission(submission)
        review.setStatus("AVAILABLE")
        submission.addReview(review)

        when:
        def result = submissionService.getSubmissionReviews(submission.getId())

        then: "returned data is correct"
        result.size() == 1
    }

    def "get submission reviews with 3 review"(){
        given: "a review"
        def review1 = new Review()
        review1.setComment(REVIEW_1_COMMENT)
        review1.setUser(teacher)
        review1.setSubmission(submission)
        review1.setStatus("AVAILABLE")
        submission.addReview(review1)
        and: "another review"
        def review2 = new Review()
        review2.setComment(REVIEW_2_COMMENT)
        review2.setUser(teacher)
        review2.setSubmission(submission)
        review2.setStatus("AVAILABLE")
        submission.addReview(review2)
        and: "another review"
        def review3 = new Review()
        review3.setComment(REVIEW_3_COMMENT)
        review3.setUser(teacher)
        review3.setSubmission(submission)
        review3.setStatus("AVAILABLE")
        submission.addReview(review3)

        when:
        def result = submissionService.getSubmissionReviews(submission.getId())

        then:"returned data is correct"
        result.size() == 3
    }

    def "get submission reviews with no review"(){
        when:
        def result = submissionService.getSubmissionReviews(submission.getId())

        then:"returned data is correct"
        result.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



