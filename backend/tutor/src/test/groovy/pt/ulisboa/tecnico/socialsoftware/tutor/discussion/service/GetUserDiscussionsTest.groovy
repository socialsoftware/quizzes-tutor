package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration

@DataJpaTest
class GetUserDiscussionsTest extends DiscussionFixtureSpockTest {
    def setup(){
        defineBaseFixture()
    }

    def "get created discussion"(){
        given: "a discussion"
        createDiscussion(questionAnswer)

        when: "tries to get the discussion"
        def discussionsResult = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())

        then: "the correct discussion is retrieved"
        discussionsResult.size() == 1
        def discussion = discussionsResult.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE

    }

    def "get discussion of invalid question"(){
        given: "an invalid question id"
        def userId = -3

        when: "getting question discussion"
        def discussions = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(), userId);

        then: "no discussion is retrieved"
        discussions.size() == 0
    }

    def "get discussion from user with no discussion"(){
        when: "tries to get the discussion"
        def discussionsResult = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(), student.getId())

        then: "no discussion is retrieved"
        discussionsResult.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}