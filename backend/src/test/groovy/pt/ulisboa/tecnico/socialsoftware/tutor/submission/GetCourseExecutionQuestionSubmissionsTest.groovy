package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetCourseExecutionQuestionSubmissionsTest extends SpockTest{
    def student1
    def student2
    def teacher
    def question
    def questionSubmission

    def setup() {
        student1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        student1.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student1)
        student2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        student2.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student2)
        teacher = new User(USER_3_NAME, USER_3_USERNAME, User.Role.TEACHER)
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
        questionSubmission.setUser(student1)
        questionSubmission.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission)
        student1.addQuestionSubmission(questionSubmission)
        questionSubmissionRepository.save(questionSubmission)

        when:
        def result = questionSubmissionService.getCourseExecutionQuestionSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 1
    }

    def "get question submissions with 3 submitted questions"(){
        given: "a question submission"
        def questionSubmission1 = new QuestionSubmission()
        questionSubmission1.setQuestion(question)
        questionSubmission1.setUser(student1)
        questionSubmission1.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission1)
        student1.addQuestionSubmission(questionSubmission1)
        questionSubmissionRepository.save(questionSubmission1)

        and: "another question submission"
        def questionSubmission2 = new QuestionSubmission()
        questionSubmission2.setQuestion(question)
        questionSubmission2.setUser(student1)
        questionSubmission2.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission2)
        student1.addQuestionSubmission(questionSubmission2)
        questionSubmissionRepository.save(questionSubmission2)

        and: "another question submission"
        def questionSubmission3 = new QuestionSubmission()
        questionSubmission3.setQuestion(question)
        questionSubmission3.setUser(student2)
        questionSubmission3.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission3)
        student2.addQuestionSubmission(questionSubmission3)
        questionSubmissionRepository.save(questionSubmission3)

        when:
        def result = questionSubmissionService.getCourseExecutionQuestionSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 3
    }

    def "get question submissions with no submitted questions"(){
        when:
        def result = questionSubmissionService.getCourseExecutionQuestionSubmissions(courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



