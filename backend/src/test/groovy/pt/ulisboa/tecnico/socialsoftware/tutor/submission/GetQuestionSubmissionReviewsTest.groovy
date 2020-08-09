package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class GetQuestionSubmissionReviewsTest extends SpockTest{
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

    def "get question submission reviews with 1 review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setQuestionSubmission(questionSubmission)
        review.setStatus(Review.Status.AVAILABLE)
        questionSubmission.addReview(review)

        when:
        def result = questionSubmissionService.getQuestionSubmissionReviews(questionSubmission.getId())

        then: "returned data is correct"
        result.size() == 1
    }

    def "get question submission reviews with 3 review"(){
        given: "a review"
        def review1 = new Review()
        review1.setComment(REVIEW_1_COMMENT)
        review1.setUser(teacher)
        review1.setQuestionSubmission(questionSubmission)
        review1.setStatus(Review.Status.AVAILABLE)
        questionSubmission.addReview(review1)
        and: "another review"
        def review2 = new Review()
        review2.setComment(REVIEW_2_COMMENT)
        review2.setUser(teacher)
        review2.setQuestionSubmission(questionSubmission)
        review2.setStatus(Review.Status.AVAILABLE)
        questionSubmission.addReview(review2)
        and: "another review"
        def review3 = new Review()
        review3.setComment(REVIEW_3_COMMENT)
        review3.setUser(teacher)
        review3.setQuestionSubmission(questionSubmission)
        review3.setStatus(Review.Status.AVAILABLE)
        questionSubmission.addReview(review3)

        when:
        def result = questionSubmissionService.getQuestionSubmissionReviews(questionSubmission.getId())

        then:"returned data is correct"
        result.size() == 3
    }

    def "get question submission reviews with no review"(){
        when:
        def result = questionSubmissionService.getQuestionSubmissionReviews(questionSubmission.getId())

        then:"returned data is correct"
        result.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



