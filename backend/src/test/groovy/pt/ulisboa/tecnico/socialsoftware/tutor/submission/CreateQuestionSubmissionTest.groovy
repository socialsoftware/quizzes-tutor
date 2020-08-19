package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Shared
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_COURSE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_STUDENT

@DataJpaTest
class CreateQuestionSubmissionTest extends SpockTest{
    @Shared
    def student
    @Shared
    def questionDto
    def teacher

    def setup() {
        student = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        student.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, User.Role.TEACHER)
        userRepository.save(teacher)
        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.IN_REVISION.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
    }

    def "create question submission with question not null"() {
        given: "a QuestionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecution.getId())
        questionSubmissionDto.setUserId(student.getId())
        questionSubmissionDto.setQuestion(questionDto)

        when: questionSubmissionService.createQuestionSubmission(questionSubmissionDto)

        then: "the correct questionSubmission is in the repository and student is enrolled in course"
        questionSubmissionRepository.count() == 1L
        def result = questionSubmissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.IN_REVISION
        result.getCourseExecution() == courseExecution
        student.getEnrolledCoursesAcronyms().contains(courseExecution.getAcronym())
    }

    @Unroll
    def "invalid arguments: userId=#userId | question#question | courseExecutionIdw=#courseExecutionId || errorMessage"(){
        given: "a QuestionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(courseExecutionId)
        questionSubmissionDto.setUserId(userId)
        questionSubmissionDto.setQuestion(question)
        when:
        questionSubmissionService.createQuestionSubmission(questionSubmissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        userId          | question     | courseExecutionId        || errorMessage
        null            | questionDto  | courseExecution.getId()  || QUESTION_SUBMISSION_MISSING_STUDENT
        student.getId() | null         | courseExecution.getId()  || QUESTION_SUBMISSION_MISSING_QUESTION
        student.getId() | questionDto  | null                     || QUESTION_SUBMISSION_MISSING_COURSE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
