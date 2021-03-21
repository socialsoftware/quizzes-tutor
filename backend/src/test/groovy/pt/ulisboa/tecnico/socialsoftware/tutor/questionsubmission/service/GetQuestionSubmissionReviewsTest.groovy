package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser


@DataJpaTest
class GetQuestionSubmissionReviewsTest extends SpockTest {
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
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmission.setStatus(QuestionSubmission.Status.IN_REVIEW)
        questionSubmissionRepository.save(questionSubmission)
    }

    def "get question submission reviews with 1 review"(){
        given: "a review"
        def review = new Review()
        review.setComment(REVIEW_1_COMMENT)
        review.setUser(teacher)
        review.setQuestionSubmission(questionSubmission)
        review.setType(Review.Type.APPROVE)
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
        rev.getName() == teacher.getName()
        rev.getUsername() == teacher.getUsername()
        rev.getType() == Review.Type.APPROVE.name()
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



