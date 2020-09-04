package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

@DataJpaTest
class ToggleInReviewStatusTest extends SpockTest{
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
        questionRepository.save(question)
        questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmissionRepository.save(questionSubmission)
    }

    @Unroll
    def "Toggle InReview Status: Initial Status = #status | Toggle = #toggle | New Status = #newStatus"() {
        given: "the question's status is #status"
        question.setStatus(status)
        questionRepository.save(question)
        questionSubmission.setQuestion(question)
        questionSubmissionRepository.save(questionSubmission)

        when:
        questionSubmissionService.toggleInReviewStatus(questionSubmission.getId(), toggle)

        then: "the question is now #newStatus"
        def question = questionRepository.findAll().get(0)
        question.getStatus() == newStatus

        where:
        status                      | toggle | newStatus
        Question.Status.IN_REVISION | true   | Question.Status.IN_REVIEW
        Question.Status.IN_REVISION | false  | Question.Status.IN_REVISION
        Question.Status.IN_REVIEW   | true   | Question.Status.IN_REVIEW
        Question.Status.IN_REVIEW   | false  | Question.Status.IN_REVISION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


