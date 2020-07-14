package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Shared
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_MISSING_COURSE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_MISSING_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_MISSING_STUDENT

@DataJpaTest
class CreateSubmissionTest extends SpockTest{
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
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
    }

    def "create submission with question not null"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setUserId(student.getId())
        submissionDto.setQuestion(questionDto)

        when: submissionService.createSubmission(submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == courseExecution
        !result.isAnonymous()
    }

    def "create submission with question not null and an argument"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setUserId(student.getId())
        submissionDto.setQuestion(questionDto)
        submissionDto.setArgument(SUBMISSION_ARGUMENT);

        when: submissionService.createSubmission(submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == courseExecution
        !result.isAnonymous()
    }

    def "create an anonymous submission with question not null"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setUserId(student.getId())
        submissionDto.setQuestion(questionDto)
        submissionDto.setAnonymous(true)

        when: submissionService.createSubmission(submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion().getTitle() == questionDto.getTitle()
        result.getQuestion().getContent() == questionDto.getContent()
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == courseExecution
        result.isAnonymous()
    }

    def "user is not a student"(){
        given: "a submissionDto for a teacher"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setUserId(teacher.getId())
        submissionDto.setQuestion(questionDto)

        when: submissionService.createSubmission(submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
    }

    def "student that submits a question enrolled in course"(){
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setUserId(student.getId())
        submissionDto.setQuestion(questionDto)

        when: submissionService.createSubmission(submissionDto)

        then:
        student.getEnrolledCoursesAcronyms().contains(courseExecution.getAcronym())
    }

    @Unroll
    def "invalid arguments: userId=#userId | question#question | courseExecutionId=#courseExecutionId || errorMessage"(){
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecutionId)
        submissionDto.setUserId(userId)
        submissionDto.setQuestion(question)
        when:
        submissionService.createSubmission(submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        userId          | question     | courseExecutionId        || errorMessage
        null            | questionDto  | courseExecution.getId()  || SUBMISSION_MISSING_STUDENT
        student.getId() | null         | courseExecution.getId()  || SUBMISSION_MISSING_QUESTION
        student.getId() | questionDto  | null                     || SUBMISSION_MISSING_COURSE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
