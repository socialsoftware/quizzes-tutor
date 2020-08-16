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
        courseExecution.addUser(student1)
        courseExecution.addUser(student2)
        courseExecution.addUser(student3)
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
        student1.addQuestionSubmission(questionSubmission1)
        questionSubmissionRepository.save(questionSubmission1)
        def questionSubmission2 = new QuestionSubmission()
        questionSubmission2.setQuestion(question)
        questionSubmission2.setUser(student2)
        questionSubmission2.setCourseExecution(courseExecution)
        student2.addQuestionSubmission(questionSubmission2)
        questionSubmissionRepository.save(questionSubmission2)
        def questionSubmission3 = new QuestionSubmission()
        questionSubmission3.setQuestion(question)
        questionSubmission3.setUser(student3)
        questionSubmission3.setCourseExecution(courseExecution)
        student3.addQuestionSubmission(questionSubmission3)
        questionSubmissionRepository.save(questionSubmission3)
        def questionSubmission4 = new QuestionSubmission()
        questionSubmission4.setQuestion(question)
        questionSubmission4.setUser(student3)
        questionSubmission4.setCourseExecution(courseExecution)
        student3.addQuestionSubmission(questionSubmission4)
        questionSubmissionRepository.save(questionSubmission4)

        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 3
        def student1Info = result.get(1)
        def student2Info = result.get(2)
        def student3Info = result.get(0)

        student1Info.getUserId() == student1.getId()
        student2Info.getUserId() == student2.getId()
        student3Info.getUserId() == student3.getId()
        student1Info.getNumQuestionSubmissions() == 1
        student2Info.getNumQuestionSubmissions() == 1
        student3Info.getNumQuestionSubmissions() == 2
        student1Info.getQuestionSubmissions().size() == 1
        student2Info.getQuestionSubmissions().size() == 1
        student3Info.getQuestionSubmissions().size() == 2
        student1Info.getUsername() == student1.getUsername()
        student2Info.getUsername() == student2.getUsername()
        student3Info.getUsername() == student3.getUsername()
        student1Info.getName() == student1.getName()
        student2Info.getName() == student2.getName()
        student3Info.getName() == student3.getName()
    }

    def "get all student question submissions info with no question submissions"() {
        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(courseExecution.getId())

        then:
        result.size() == 3
        def student1Info = result.get(0)
        def student2Info = result.get(1)
        def student3Info = result.get(2)

        student1Info.getUserId() == student1.getId()
        student2Info.getUserId() == student2.getId()
        student3Info.getUserId() == student3.getId()
        student1Info.getNumQuestionSubmissions() == 0
        student2Info.getNumQuestionSubmissions() == 0
        student3Info.getNumQuestionSubmissions() == 0
        student1Info.getQuestionSubmissions().size() == 0
        student2Info.getQuestionSubmissions().size() == 0
        student3Info.getQuestionSubmissions().size() == 0
        student1Info.getUsername() == student1.getUsername()
        student2Info.getUsername() == student2.getUsername()
        student3Info.getUsername() == student3.getUsername()
        student1Info.getName() == student1.getName()
        student2Info.getName() == student2.getName()
        student3Info.getName() == student3.getName()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




