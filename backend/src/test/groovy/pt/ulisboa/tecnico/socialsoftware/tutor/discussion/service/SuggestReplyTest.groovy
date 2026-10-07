package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_NOT_FOUND

@DataJpaTest
class SuggestReplyTest extends DiscussionFixtureSpockTest {
    def teacher
    def discussion

    def setup() {
        aqgClient.reset()
        aqgClient.suggestionReply = new AqgDiscussionSuggestionDto('A draft', ['Networks > HTTP'])

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_1_EMAIL, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        defineBaseFixture()
        discussion = createDiscussion(questionAnswer)
    }

    def "the draft of the service is returned and nothing is saved"() {
        when:
        def suggestion = discussionSuggestionService.suggestReply(discussion.getId())

        then:
        suggestion.reply() == 'A draft'
        suggestion.sources() == ['Networks > HTTP']
        replyRepository.count() == 0L
        discussion.getReplies().isEmpty()
    }

    def "the service gets the question, the options, the choice and the doubt"() {
        when:
        discussionSuggestionService.suggestReply(discussion.getId())

        then:
        def sent = aqgClient.lastSuggestionRequest
        sent.courseId() == externalCourse.getId()
        sent.questionStem() == QUESTION_1_CONTENT
        sent.options().collect { it.content() } as Set == [OPTION_1_CONTENT, OPTION_2_CONTENT] as Set
        sent.options().find { it.content() == OPTION_1_CONTENT }.correct()
        sent.studentChoice() == OPTION_1_CONTENT
        sent.studentMessage() == DISCUSSION_MESSAGE
        sent.replies() == []
        sent.materialSections() == [:]
    }

    def "the doubt is the last message of the student and what came before is the conversation"() {
        given:
        addReplyToDiscussion(teacher, discussion, false)
        def followUp = addReplyToDiscussion(student, discussion, false)
        followUp.setMessage('But why not the other one?')

        when:
        discussionSuggestionService.suggestReply(discussion.getId())

        then:
        def sent = aqgClient.lastSuggestionRequest
        sent.studentMessage() == 'But why not the other one?'
        sent.replies().collect { it.role() } == ['student', 'teacher']
        sent.replies()[1].message() == DISCUSSION_REPLY
    }

    def "when the teacher answered last, the doubt is still the last one of the student"() {
        given:
        addReplyToDiscussion(teacher, discussion, false)

        when:
        discussionSuggestionService.suggestReply(discussion.getId())

        then:
        def sent = aqgClient.lastSuggestionRequest
        sent.studentMessage() == DISCUSSION_MESSAGE
        sent.replies() == []
    }

    def "no name or username of anyone is sent"() {
        given:
        addReplyToDiscussion(teacher, discussion, false)

        when:
        discussionSuggestionService.suggestReply(discussion.getId())

        then:
        def text = aqgClient.lastSuggestionRequest.toString()
        !text.contains(USER_1_NAME) && !text.contains(USER_1_USERNAME) && !text.contains(USER_1_EMAIL)
        !text.contains(USER_2_NAME) && !text.contains(USER_2_USERNAME)
    }

    def "the sections of the topics of the question are sent so the draft can use the course material"() {
        given:
        def topicDto = new TopicDto()
        topicDto.setName('Networks')
        def topic = topicService.createTopic(externalCourse.getId(), topicDto)
        topicService.updateTopicSources(topic.getId(), [new TopicSourceDto('m1', 'Networks > HTTP'),
                                                         new TopicSourceDto('m1', 'Networks > DNS')])
        def question = discussion.getQuestion()
        question.addTopic(topicRepository.findById(topic.getId()).get())

        when:
        discussionSuggestionService.suggestReply(discussion.getId())

        then:
        aqgClient.lastSuggestionRequest.materialSections() == [m1: ['Networks > HTTP', 'Networks > DNS']]
    }

    def "an unknown discussion is refused before calling the service"() {
        when:
        discussionSuggestionService.suggestReply(-1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DISCUSSION_NOT_FOUND
        aqgClient.lastSuggestionRequest == null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
