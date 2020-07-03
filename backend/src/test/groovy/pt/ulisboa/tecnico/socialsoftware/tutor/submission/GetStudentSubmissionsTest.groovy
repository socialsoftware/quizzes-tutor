package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetStudentSubmissionsTest extends SpockTest{
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
    }

    def "get submissions with 1 submitted question"(){
        given: "a submission"
        def submission = new Submission()
        submission.setQuestion(question)
        submission.setUser(student)
        submission.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission)
        student.addSubmission(submission)
        submissionRepository.save(submission)

        when:
        def result = submissionService.getStudentSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 1
    }

    def "get submissions with 3 submitted questions"(){
        given: "a submission"
        def submission1 = new Submission()
        submission1.setQuestion(question)
        submission1.setUser(student)
        submission1.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission1)
        student.addSubmission(submission1)
        submissionRepository.save(submission1)

        and: "another submission"
        def submission2 = new Submission()
        submission2.setQuestion(question)
        submission2.setUser(student)
        submission2.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission2)
        student.addSubmission(submission2)
        submissionRepository.save(submission2)

        and: "another submission"
        def submission3 = new Submission()
        submission3.setQuestion(question)
        submission3.setUser(student)
        submission3.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission3)
        student.addSubmission(submission3)
        submissionRepository.save(submission3)

        when:
        def result = submissionService.getStudentSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 3
    }

    def "get submissions with no submitted questions"(){
        when:
        def result = submissionService.getStudentSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



