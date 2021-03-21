package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class ChangeStatusTest extends DiscussionFixtureSpockTest {
    def teacher
    def discussion

    def setup(){
        createExternalCourseAndExecution()

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        defineBaseFixture()

        discussion = createDiscussion(questionAnswer)

        addReplyToDiscussion(student, discussion, false)
    }

    def "changes discussion with replies status to closed"(){
        when: "change open discussion with replies status"
        discussionService.changeStatus(discussion.getId())

        then: "the status has changed"
        def resultDiscussionList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
        resultDiscussionList.size() == 1
        def discussion = resultDiscussionList.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE
        discussion.getQuestion().getId() == question1.getId()
        discussion.isClosed() == true
    }

    def "changes discussion with no replies status to closed"(){
        given: "a discussion with no replies"
        def discussionNoReplies = createDiscussion(questionAnswer2)

        when: "change open discussion status"
        discussionService.changeStatus(discussionNoReplies.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLOSE_NOT_POSSIBLE
    }

    def "changes closed discussion with replies"(){
        given: 'a discussion that is closed'
        discussion.setClosed(true)

        when: "change open discussion with replies status"
        discussionService.changeStatus(discussion.getId())

        then: "the status remains the same"
        def resultDiscussionList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
        resultDiscussionList.size() == 1
        def discussion = resultDiscussionList.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE
        discussion.getQuestion().getId() == question1.getId()
        discussion.isClosed() == false
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
