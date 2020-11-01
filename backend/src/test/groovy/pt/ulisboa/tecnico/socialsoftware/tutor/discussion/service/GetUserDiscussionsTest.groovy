package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

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