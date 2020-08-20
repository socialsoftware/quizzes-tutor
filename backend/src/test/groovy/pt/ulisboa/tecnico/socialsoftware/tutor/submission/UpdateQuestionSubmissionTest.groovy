package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateQuestionSubmissionTest extends SpockTest{
    def student
    def teacher
    def question
    def optionOK
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
        question.setStatus(Question.Status.IN_REVISION)
        questionRepository.save(question)
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setUser(student)
        questionSubmission.setCourseExecution(courseExecution)
        courseExecution.addQuestionSubmission(questionSubmission)
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

        when:
        questionSubmissionService.updateQuestionSubmission(questionSubmission.getId(), questionSubmissionDto)

        then: "the question submission is changed"
        questionSubmissionRepository.count() == 1L
        def result = questionSubmissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.IN_REVISION
        result.getCourseExecution() == courseExecution
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
        given: "a question IN_REVIEW"
        question.setStatus(status)
        questionRepository.save(question)
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
        exception.getErrorMessage() == ErrorMessage.CANNOT_EDIT_REVIEWED_QUESTION

        where:
        status << [Question.Status.AVAILABLE, Question.Status.DISABLED, Question.Status.REJECTED, Question.Status.IN_REVIEW]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




