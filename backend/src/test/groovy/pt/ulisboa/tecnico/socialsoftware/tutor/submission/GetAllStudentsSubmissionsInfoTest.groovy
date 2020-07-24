package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetAllStudentsSubmissionsInfoTest extends SpockTest{
    def student1
    def student2
    def student3
    def question

    def setup() {
        student1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        student1.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student1)
        student2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        student2.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student2)
        student3 = new User(USER_3_NAME, USER_3_USERNAME, User.Role.STUDENT)
        student3.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student3)
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setCourse(course)
        question.setStatus(Question.Status.IN_REVIEW)
        questionRepository.save(question)
    }

    def "get all student submissions info"() {
        given: "submissions"
        def submission1 = new Submission()
        submission1.setQuestion(question)
        submission1.setUser(student1)
        submission1.setCourseExecution(courseExecution)
        submissionRepository.save(submission1)
        def submission2 = new Submission()
        submission2.setQuestion(question)
        submission2.setUser(student2)
        submission2.setCourseExecution(courseExecution)
        submissionRepository.save(submission2)
        def submission3 = new Submission()
        submission3.setQuestion(question)
        submission3.setUser(student3)
        submission3.setCourseExecution(courseExecution)
        submissionRepository.save(submission3)

        when: def result = submissionService.getAllStudentsSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 3

    }

    def "get all student submissions info with no submissions"() {

        when: def result = submissionService.getAllStudentsSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 0

    }

        @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




