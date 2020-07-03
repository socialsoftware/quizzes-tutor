package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetAllStudentsSubmissionsTest extends SpockTest{
    def student1
    def student2
    def student3
    def question
    def submission

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

    def "get submissions with 1 annonymous and 1 non anonymous submitted questions"(){
        given: "a submission"
        def submission1 = new Submission()
        submission1.setQuestion(question)
        submission1.setUser(student1)
        submission1.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission1)
        student1.addSubmission(submission1)
        submissionRepository.save(submission1)
        and: "an anonymous submission"
        def submission2 = new Submission()
        submission2.setQuestion(question)
        submission2.setUser(student1)
        submission2.setCourseExecution(courseExecution)
        submission2.setAnonymous(true)
        courseExecution.addSubmission(submission2)
        student2.addSubmission(submission2)
        submissionRepository.save(submission2)

        when:
        def result = submissionService.getAllStudentsSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 2
        def sub1 = result.get(0)
        def sub2 = result.get(1)
        sub1.getStudentId() == student1.getId()
        !sub1.isAnonymous()
        sub2.getStudentId() == student2.getId()
        sub2.isAnonymous()
    }

    def "get submissions with 3 annonymous submitted questions"(){
        given: "a submission"
        def submission1 = new Submission()
        submission1.setQuestion(question)
        submission1.setUser(student1)
        submission1.setCourseExecution(courseExecution)
        submission1.setAnonymous(true)
        courseExecution.addSubmission(submission1)
        student1.addSubmission(submission1)
        submissionRepository.save(submission1)
        and: "an anonymous submission"
        def submission2 = new Submission()
        submission2.setQuestion(question)
        submission2.setUser(student2)
        submission2.setCourseExecution(courseExecution)
        submission2.setAnonymous(true)
        courseExecution.addSubmission(submission2)
        student2.addSubmission(submission2)
        submissionRepository.save(submission2)
        and: "an anonymous submission"
        def submission3 = new Submission()
        submission3.setQuestion(question)
        submission3.setUser(student3)
        submission3.setCourseExecution(courseExecution)
        submission3.setAnonymous(true)
        courseExecution.addSubmission(submission3)
        student3.addSubmission(submission3)
        submissionRepository.save(submission3)

        when:
        def result = submissionService.getAllStudentsSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 3
        def sub1 = result.get(0)
        def sub2 = result.get(1)
        def sub3 = result.get(2)
        sub1.getStudentId() == student1.getId()
        sub1.isAnonymous()
        sub2.getStudentId() == student2.getId()
        sub2.isAnonymous()
        sub3.getStudentId() == student3.getId()
        sub3.isAnonymous()
    }

    def "get submissions with 3 non annonymous submitted questions"(){
        given: "a submission"
        def submission1 = new Submission()
        submission1.setQuestion(question)
        submission1.setUser(student1)
        submission1.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission1)
        student1.addSubmission(submission1)
        submissionRepository.save(submission1)
        and: "an anonymous submission"
        def submission2 = new Submission()
        submission2.setQuestion(question)
        submission2.setUser(student2)
        submission2.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission2)
        student2.addSubmission(submission2)
        submissionRepository.save(submission2)
        and: "an anonymous submission"
        def submission3 = new Submission()
        submission3.setQuestion(question)
        submission3.setUser(student3)
        submission3.setCourseExecution(courseExecution)
        courseExecution.addSubmission(submission3)
        student3.addSubmission(submission3)
        submissionRepository.save(submission3)

        when:
        def result = submissionService.getAllStudentsSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 3
        def sub1 = result.get(0)
        def sub2 = result.get(1)
        def sub3 = result.get(2)
        sub1.getStudentId() == student1.getId()
        !sub1.isAnonymous()
        sub2.getStudentId() == student2.getId()
        !sub2.isAnonymous()
        sub3.getStudentId() == student3.getId()
        !sub3.isAnonymous()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




