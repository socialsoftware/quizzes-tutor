package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetStudentQuestionSubmissionsTest extends SpockTest{
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
    }

    def "get question submissions with 1 submitted question"(){
        given: "a question submission"
        def questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setUser(student)
        questionSubmission.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission)
        student.addQuestionSubmission(questionSubmission)
        questionSubmissionRepository.save(questionSubmission)

        when:
        def result = questionSubmissionService.getStudentQuestionSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 1
    }

    def "get question submissions with 3 submitted questions"(){
        given: "a question submission"
        def questionSubmission1 = new QuestionSubmission()
        questionSubmission1.setQuestion(question)
        questionSubmission1.setUser(student)
        questionSubmission1.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission1)
        student.addQuestionSubmission(questionSubmission1)
        questionSubmissionRepository.save(questionSubmission1)

        and: "another question submission"
        def questionSubmission2 = new QuestionSubmission()
        questionSubmission2.setQuestion(question)
        questionSubmission2.setUser(student)
        questionSubmission2.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission2)
        student.addQuestionSubmission(questionSubmission2)
        questionSubmissionRepository.save(questionSubmission2)

        and: "another question submission"
        def questionSubmission3 = new QuestionSubmission()
        questionSubmission3.setQuestion(question)
        questionSubmission3.setUser(student)
        questionSubmission3.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission3)
        student.addQuestionSubmission(questionSubmission3)
        questionSubmissionRepository.save(questionSubmission3)

        when:
        def result = questionSubmissionService.getStudentQuestionSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 3
    }

    def "get question submissions with no submitted questions"(){
        when:
        def result = questionSubmissionService.getStudentQuestionSubmissions(student.getId(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



