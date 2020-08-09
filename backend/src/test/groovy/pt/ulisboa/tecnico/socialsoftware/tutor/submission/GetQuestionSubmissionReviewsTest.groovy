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
        def rev = result.get(0)

        rev.getId() != null
        rev.getUserId() == teacher.getId()
        rev.getQuestionSubmissionId() == questionSubmission.getId()
        rev.getComment() == REVIEW_1_COMMENT
        rev.getStatus() == Review.Status.AVAILABLE.name()
        rev.getName() == teacher.getName()
        rev.getUsername() == teacher.getUsername()
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



