package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetAllStudentsQuestionSubmissionsInfoTest extends SpockTest{
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

    def "get all student question submissions info"() {
        given: "questionSubmissions"
        def questionSubmission1 = new QuestionSubmission()
        questionSubmission1.setQuestion(question)
        questionSubmission1.setUser(student1)
        questionSubmission1.setCourseExecution(courseExecution)
        questionSubmissionRepository.save(questionSubmission1)
        def questionSubmission2 = new QuestionSubmission()
        questionSubmission2.setQuestion(question)
        questionSubmission2.setUser(student2)
        questionSubmission2.setCourseExecution(courseExecution)
        questionSubmissionRepository.save(questionSubmission2)
        def questionSubmission3 = new QuestionSubmission()
        questionSubmission3.setQuestion(question)
        questionSubmission3.setUser(student3)
        questionSubmission3.setCourseExecution(courseExecution)
        questionSubmissionRepository.save(questionSubmission3)

        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 3
        def studentInfo1 = result.get(0)
        def studentInfo2 = result.get(1)
        def studentInfo3 = result.get(2)

        studentInfo1.getUserId() == student1.getId()
        studentInfo2.getUserId() == student2.getId()
        studentInfo3.getUserId() == student3.getId()
        studentInfo1.getNumQuestionSubmissions() == 1
        studentInfo2.getNumQuestionSubmissions() == 1
        studentInfo3.getNumQuestionSubmissions() == 1
        studentInfo1.getUsername() == student1.getUsername()
        studentInfo2.getUsername() == student2.getUsername()
        studentInfo3.getUsername() == student3.getUsername()
        studentInfo1.getName() == student1.getName()
        studentInfo2.getName() == student2.getName()
        studentInfo3.getName() == student3.getName()
    }

    def "get all student question submissions info with no question submissions"() {
        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




