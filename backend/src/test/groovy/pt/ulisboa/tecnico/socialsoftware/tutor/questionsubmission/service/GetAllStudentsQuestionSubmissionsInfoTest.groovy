package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class GetAllStudentsQuestionSubmissionsInfoTest extends SpockTest {
    def student1
    def student2
    def student3
    def question

    def setup() {
        createExternalCourseAndExecution()

        student1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student1)
        student2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student2)
        student3 = new User(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student3)
        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)
        externalCourseExecution.addUser(student3)
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setCourse(externalCourse)
        question.setStatus(Question.Status.SUBMITTED)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        def optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        questionRepository.save(question)
    }

    def "get all student question submissions info"() {
        given: "questionSubmissions"
        def questionSubmission1 = new QuestionSubmission()
        questionSubmission1.setQuestion(question)
        questionSubmission1.setSubmitter(student1)
        questionSubmission1.setCourseExecution(externalCourseExecution)
        questionSubmission1.setStatus(QuestionSubmission.Status.IN_REVIEW)
        student1.addQuestionSubmission(questionSubmission1)
        questionSubmissionRepository.save(questionSubmission1)
        def questionSubmission2 = new QuestionSubmission()
        questionSubmission2.setQuestion(question)
        questionSubmission2.setSubmitter(student2)
        questionSubmission2.setCourseExecution(externalCourseExecution)
        questionSubmission2.setStatus(QuestionSubmission.Status.IN_REVIEW)
        student2.addQuestionSubmission(questionSubmission2)
        questionSubmissionRepository.save(questionSubmission2)
        def questionSubmission3 = new QuestionSubmission()
        questionSubmission3.setQuestion(question)
        questionSubmission3.setSubmitter(student3)
        questionSubmission3.setCourseExecution(externalCourseExecution)
        questionSubmission3.setStatus(QuestionSubmission.Status.IN_REVIEW)
        student3.addQuestionSubmission(questionSubmission3)
        questionSubmissionRepository.save(questionSubmission3)
        def questionSubmission4 = new QuestionSubmission()
        questionSubmission4.setQuestion(question)
        questionSubmission4.setSubmitter(student3)
        questionSubmission4.setCourseExecution(externalCourseExecution)
        questionSubmission4.setStatus(QuestionSubmission.Status.IN_REVIEW)
        student3.addQuestionSubmission(questionSubmission4)
        questionSubmissionRepository.save(questionSubmission4)

        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(externalCourseExecution.getId())

        then:
        result.size() == 3
        def student1Info = result.get(1)
        def student2Info = result.get(2)
        def student3Info = result.get(0)

        student1Info.getSubmitterId() == student1.getId()
        student2Info.getSubmitterId() == student2.getId()
        student3Info.getSubmitterId() == student3.getId()
        student1Info.getTotalQuestionSubmissions() == 1
        student2Info.getTotalQuestionSubmissions() == 1
        student3Info.getTotalQuestionSubmissions() == 2
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
        when: def result = questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(externalCourseExecution.getId())

        then:
        result.size() == 3
        def student1Info = result.get(0)
        def student2Info = result.get(1)
        def student3Info = result.get(2)

        student1Info.getSubmitterId() == student1.getId()
        student2Info.getSubmitterId() == student2.getId()
        student3Info.getSubmitterId() == student3.getId()
        student1Info.getTotalQuestionSubmissions() == 0
        student2Info.getTotalQuestionSubmissions() == 0
        student3Info.getTotalQuestionSubmissions() == 0
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




