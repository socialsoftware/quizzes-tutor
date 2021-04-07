package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateQuestionSubmissionTest extends SpockTest {
    def student
    def teacher
    def question
    def optionOK
    def questionSubmission

    def setup() {
        createExternalCourseAndExecution()

        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setCourse(externalCourse)
        question.setStatus(Question.Status.SUBMITTED)
        questionRepository.save(question)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmission.setStatus(QuestionSubmission.Status.IN_REVISION)
        externalCourseExecution.addQuestionSubmission(questionSubmission)
        student.addQuestionSubmission(questionSubmission)
        questionSubmissionRepository.save(questionSubmission)
    }

    def "edit a question submission"(){
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a questionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto(questionSubmission)
        questionSubmissionDto.setQuestion(questionDto)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();
        
        when:
        questionSubmissionService.updateQuestionSubmission(questionSubmission.getId(), questionSubmissionDto)

        then: "the question submission is changed"
        questionSubmissionRepository.count() == 1L
        def result = questionSubmissionRepository.findAll().get(0)
        result.getId() != null
        result.getSubmitter() == student
        result.getStatus() == QuestionSubmission.Status.IN_REVISION
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == externalCourseExecution
    }

    @Unroll
    def "edit a question with invalid input"() {
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(title)
        questionDto.setContent(content)
        and: "a questionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto(questionSubmission)
        questionSubmissionDto.setQuestion(questionDto)

        when:
        questionSubmissionService.updateQuestionSubmission(questionSubmission.getId(), questionSubmissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        title            | content            | errorMessage
        null             | QUESTION_2_CONTENT | INVALID_TITLE_FOR_QUESTION
        ' '              | QUESTION_2_CONTENT | INVALID_TITLE_FOR_QUESTION
        QUESTION_2_TITLE | null               | INVALID_CONTENT_FOR_QUESTION
        QUESTION_2_TITLE | ' '                | INVALID_CONTENT_FOR_QUESTION
    }

    @Unroll
    def "edit a question not in revision"(){
        given: "a question submission not in revision"
        questionSubmission.setStatus(status)
        and: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a questionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto(questionSubmission)
        questionSubmissionDto.setQuestion(questionDto)

        when:
        questionSubmissionService.updateQuestionSubmission(questionSubmission.getId(), questionSubmissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_EDIT_REVIEWED_QUESTION

        where:
        status << [QuestionSubmission.Status.APPROVED, QuestionSubmission.Status.REJECTED, QuestionSubmission.Status.IN_REVIEW]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




