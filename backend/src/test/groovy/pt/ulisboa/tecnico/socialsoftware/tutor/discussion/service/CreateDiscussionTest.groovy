package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll

@DataJpaTest
class CreateDiscussionTest extends DiscussionFixtureSpockTest {

    def setup(){
        defineBaseFixture()
    }

    def "create discussion"(){
        given:"a discussion dto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUsername(student.getUsername())
        discussionDto.setName(student.getName())

        when: "discussion is created"
        discussionService.createDiscussion(questionAnswer.getId(), discussionDto)

        then: "the correct discussion is inside the repository"
        discussionRepository.count() == 1L
        def resultUserList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
        resultUserList.size() == 1
        def resultUser = resultUserList.get(0)
        resultUser.getMessage() == DISCUSSION_MESSAGE
        resultUser.getQuestion().getId() == question1.getId()
    }

   def "user can't create two discussions for the same question"(){
       given: "a discussion dto"
       def discussionDto = new DiscussionDto()
       discussionDto.setMessage(DISCUSSION_MESSAGE)
       discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
       discussionDto.setUsername(student.getUsername())
       discussionDto.setName(student.getName())
       and: "a discussion for a question answer"
       createDiscussion(questionAnswer)

       when: "creating a discussions on the same question answer"
       discussionService.createDiscussion(questionAnswer.getId(), discussionDto)

       then: "exception is thrown"
       def exception = thrown(TutorException)
       exception.getErrorMessage() == ErrorMessage.DUPLICATE_DISCUSSION
   }

    @Unroll
    def "invalid arguments:  message=#message | date=#date"(){
        given: "a discusssionDto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(message)
        discussionDto.setDate(date)

        when: "creating discussion"
        discussionService.createDiscussion(questionAnswer.getId(), discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        message            | date                                      || errorMessage
        null               | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE
        "          "       | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}