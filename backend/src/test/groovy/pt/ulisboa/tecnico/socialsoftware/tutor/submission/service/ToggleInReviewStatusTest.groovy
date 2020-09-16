package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser
import spock.lang.Unroll

@DataJpaTest
class ToggleInReviewStatusTest extends SpockTest{
    def student
    def teacher
    def question
    def questionSubmission

    def setup() {
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
        questionSubmissionRepository.save(questionSubmission)
    }

    @Unroll
    def "Toggle InReview Status: Initial Status = #status | Toggle = #toggle | New Status = #newStatus"() {
        given: "the question submission's status is #status"
        questionSubmission.setStatus(status)

        when:
        questionSubmissionService.toggleInReviewStatus(questionSubmission.getId(), toggle)

        then: "the question is now #newStatus"
        def question = questionRepository.findAll().get(0)
        questionSubmission.getStatus() == newStatus

        where:
        status                                | toggle | newStatus
        QuestionSubmission.Status.IN_REVISION | true   | QuestionSubmission.Status.IN_REVIEW
        QuestionSubmission.Status.IN_REVISION | false  | QuestionSubmission.Status.IN_REVISION
        QuestionSubmission.Status.IN_REVIEW   | true   | QuestionSubmission.Status.IN_REVIEW
        QuestionSubmission.Status.IN_REVIEW   | false  | QuestionSubmission.Status.IN_REVISION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


