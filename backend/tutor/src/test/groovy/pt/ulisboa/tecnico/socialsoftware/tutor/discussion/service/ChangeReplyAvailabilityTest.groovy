package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class ChangeReplyAvailabilityTest extends DiscussionFixtureSpockTest {
    def teacher
    def discussion

    def setup(){
        createExternalCourseAndExecution()

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        defineBaseFixture()

        discussion = createDiscussion(questionAnswer)
    }

    def "changes reply to available"(){
        given: "a not public reply"
        def reply = addReplyToDiscussion(teacher, discussion, false)

        when: "change reply availability"
        discussionService.changeReplyAvailability(reply.getId())

        then: "reply is public"
        replyRepository.count() == 1L
        def result = replyRepository.findById(reply.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == teacher.getId()
        result.isPublic() == true
    }

    def "changes reply to not available"(){
        given: "a public reply"
        def reply = addReplyToDiscussion(teacher, discussion, true)

        when: "change reply availability"
        discussionService.changeReplyAvailability(reply.getId())

        then: "reply is not public"
        replyRepository.count() == 1L
        def result = replyRepository.findById(reply.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == teacher.getId()
        result.isPublic() == false
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
