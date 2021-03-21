package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Shared
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_COURSE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_STUDENT

@DataJpaTest
class CreateQuestionSubmissionTest extends SpockTest {
    @Shared
    def student
    @Shared
    def questionDto
    def teacher

    def setup() {
        createExternalCourseAndExecution()

        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        ((AuthTecnicoUser)student.authUser).setEnrolledCoursesAcronyms(externalCourseExecution.getAcronym())
        userRepository.save(student)
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.getQuestionDetailsDto().setOptions(options)
    }

    def "create question submission with question not null"() {
        given: "a QuestionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(externalCourseExecution.getId())
        questionSubmissionDto.setSubmitterId(student.getId())
        questionSubmissionDto.setQuestion(questionDto)

        when: questionSubmissionService.createQuestionSubmission(questionSubmissionDto)

        then: "the correct questionSubmission is in the repository and student is enrolled in externalCourse"
        questionSubmissionRepository.count() == 1L
        def result = questionSubmissionRepository.findAll().get(0)
        result.getId() != null
        result.getSubmitter() == student
        result.getQuestion() != null
        result.getStatus() == QuestionSubmission.Status.IN_REVISION
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == externalCourseExecution
        ((AuthTecnicoUser)student.authUser).getEnrolledCoursesAcronyms().contains(externalCourseExecution.getAcronym())
    }

    @Unroll
    def "invalid arguments: submitterId=#submitterId | question#question | externalCourseExecutionIdw=#externalCourseExecutionId || errorMessage"(){
        given: "a QuestionSubmissionDto"
        def questionSubmissionDto = new QuestionSubmissionDto()
        questionSubmissionDto.setCourseExecutionId(externalCourseExecutionId)
        questionSubmissionDto.setSubmitterId(submitterId)
        questionSubmissionDto.setQuestion(question)
        when:
        questionSubmissionService.createQuestionSubmission(questionSubmissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        submitterId     | question     | externalCourseExecutionId        || errorMessage
        null            | questionDto  | externalCourseExecution.getId()  || QUESTION_SUBMISSION_MISSING_STUDENT
        student.getId() | null         | externalCourseExecution.getId()  || QUESTION_SUBMISSION_MISSING_QUESTION
        student.getId() | questionDto  | null                             || QUESTION_SUBMISSION_MISSING_COURSE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
