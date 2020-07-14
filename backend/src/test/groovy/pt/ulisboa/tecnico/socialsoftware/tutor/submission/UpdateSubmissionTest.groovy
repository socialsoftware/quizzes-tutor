package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class UpdateSubmissionTest extends SpockTest{
    def student
    def teacher
    def question
    def optionOK
    def submission

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
        question.setStatus(Question.Status.SUBMITTED)
        questionRepository.save(question)
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        submission = new Submission()
        submission.setQuestion(question)
        submission.setUser(student)
        submission.setCourseExecution(courseExecution)
        submission.setArgument(SUBMISSION_1_ARGUMENT)
        courseExecution.addSubmission(submission)
        student.addSubmission(submission)
        submissionRepository.save(submission)
    }

    def "edit a submission with valid argument"(){
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a submissionDto"
        def submissionDto = new SubmissionDto(submission)
        submissionDto.setQuestion(questionDto)
        submissionDto.setArgument(SUBMISSION_2_ARGUMENT)

        when:
        submissionService.updateSubmission(submission.getId(), submissionDto)

        then: "the submission is changed"
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
        result.getArgument() == SUBMISSION_2_ARGUMENT
    }

    def "edit a submission with invalid argument"(){
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a submissionDto"
        def submissionDto = new SubmissionDto(submission)
        submissionDto.setQuestion(questionDto)
        submissionDto.setArgument(SUBMISSION_1_ARGUMENT)

        when:
        submissionService.updateSubmission(submission.getId(), submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_ARGUMENT_FOR_SUBMISSION

    }

    def "edit a submission with argument null"(){
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a submissionDto"
        def submissionDto = new SubmissionDto(submission)
        submissionDto.setQuestion(questionDto)
        submissionDto.setArgument(null);

        when:
        submissionService.updateSubmission(submission.getId(), submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ARGUMENT_MISSING_EDIT_SUBMISSION

    }

    def "edit a submission with empty argument"(){
        given: "an edited questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        and: "a submissionDto"
        def submissionDto = new SubmissionDto(submission)
        submissionDto.setQuestion(questionDto)
        submissionDto.setArgument('');

        when:
        submissionService.updateSubmission(submission.getId(), submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ARGUMENT_MISSING_EDIT_SUBMISSION

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




