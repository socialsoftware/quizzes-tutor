package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateReplyTest extends DiscussionFixtureSpockTest {
    def student2
    def teacher
    def discussion

    def setup(){
        student2 = new User(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student2)

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        defineBaseFixture()

        discussion = createDiscussion(questionAnswer)
    }

    def "teacher replies to discussion"(){
        given: "a reply"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "a reply is given"
        discussionService.addReply(teacher.getId(), discussion.getId(), replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == teacher.getId()
        discussion.getReplies().size() == 1
        discussion.getReplies().get(0).getMessage() == replyDto.getMessage()
        discussion.getReplies().get(0).getUser().getId() == teacher.getId()
    }

    def "student replies to his discussion"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student creates a reply"
        discussionService.addReply(student.getId(), discussion.getId(), replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == student.getId()
        discussion.getReplies().size() == 1
        discussion.getReplies().get(0).getMessage() == replyDto.getMessage()
        discussion.getReplies().get(0).getUser().getId() == student.getId()
    }

    def "student can't reply a discussion of other student"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student tries to create a reply"
        discussionService.addReply(student2.getId(), discussion.getId(), replyDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.REPLY_UNAUTHORIZED_USER
    }

    @Unroll
    def "invalid arguments: message=#message"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(message)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student tries to create a reply"
        discussionService.addReply(student.getId(), discussion.getId(), replyDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        message            || errorMessage
        null               || ErrorMessage.REPLY_MISSING_MESSAGE
        "          "       || ErrorMessage.REPLY_MISSING_MESSAGE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}