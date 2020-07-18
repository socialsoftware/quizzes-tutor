package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class ToggleInReviewStatusTest extends SpockTest{
    def student
    def teacher
    def question

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
        questionRepository.save(question)
    }

    def "open review status for a submitted question"(){
        given: "the question's status is IN_REVISION"
        question.setStatus(Question.Status.IN_REVISION)
        questionRepository.save(question)

        when:
        submissionService.toggleInReviewStatus(question.getId(), true)

        then: "question is in review"
        def question = questionRepository.findAll().get(0)
        question.getStatus() == Question.Status.IN_REVIEW
    }

    def "open review status for a submitted question already in review"(){
        given: "the question's status is IN_REVIEW"
        question.setStatus(Question.Status.IN_REVIEW)
        questionRepository.save(question)

        when:
        submissionService.toggleInReviewStatus(question.getId(), true)

        then: "question is still in review"
        def question = questionRepository.findAll().get(0)
        question.getStatus() == Question.Status.IN_REVIEW
    }

    def "close review status for a submitted question"(){
        given: "the question's status is IN_REVIEW"
        question.setStatus(Question.Status.IN_REVIEW)
        questionRepository.save(question)

        when:
        submissionService.toggleInReviewStatus(question.getId(), false)

        then: "question is not in review anymore"
        def question = questionRepository.findAll().get(0)
        question.getStatus() == Question.Status.IN_REVISION
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


