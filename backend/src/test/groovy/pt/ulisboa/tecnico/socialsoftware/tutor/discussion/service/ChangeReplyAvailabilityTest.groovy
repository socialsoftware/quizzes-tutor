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
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared

@DataJpaTest
class ChangeReplyAvailabilityTest extends SpockTest {
    @Shared
    def student
    def teacher
    @Shared
    def question1
    def discussionDto
    def replyDto
    def replyDto2

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student)

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setTitle("Question title")
        question1.setContent("Question Content")
        questionRepository.save(question1)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType("TEST")
        quiz.setCourseExecution(courseExecutionRepository.findAll().get(0))
        quizRepository.save(quiz)

        def quizanswer = new QuizAnswer()
        quizanswer.setUser(student)
        quizanswer.setQuiz(quiz)
        quizanswer.setQuiz(quizRepository.findAll().get(0))
        quizAnswerRepository.save(quizanswer)

        def quizquestion = new QuizQuestion(quizRepository.findAll().get(0), question1, 0)
        quizQuestionRepository.save(quizquestion)

        def questionanswer = new QuestionAnswer()
        questionanswer.setTimeTaken(1)
        questionanswer.setQuizAnswer(quizanswer)
        questionanswer.setQuizQuestion(quizquestion)
        questionAnswerRepository.save(questionanswer)


        quizquestion.addQuestionAnswer(questionAnswerRepository.findAll().get(0))

        quizanswer.addQuestionAnswer(questionAnswerRepository.findAll().get(0))


        quiz.addQuizAnswer(quizAnswerRepository.findAll().get(0))
        quiz.addQuizQuestion(quizQuestionRepository.findAll().get(0))
        quiz.setCourseExecution(courseExecutionRepository.findAll().get(0))


        student.addQuizAnswer(quizAnswerRepository.findAll().get(0))

        discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())
        discussionDto.setAvailable(false)
        discussionDto.setClosed(false)
        discussionService.createDiscussion(quizanswer.getId(), 0, discussionDto)
        discussionDto.setId(discussionRepository.findAll().get(0).getId())
    }

    def "changes reply to available with discussion not available"(){
        given: "reply not available"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto.getId())

        then: "the availability has changed"
        replyRepository.count() == 1L
        def result = replyRepository.findById(replyDto.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isAvailable() == true
        def resultDiscussion = discussionRepository.findById(discussionDto.getId()).get()
        resultDiscussion.available == true
    }

    def "changes reply to available with discussion available"(){
        given: "an available discussion"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
        //turns discussion to available
        discussionService.changeReplyAvailability(replyDto.getId())

        and: "a non available reply"
        replyDto2 = new ReplyDto()
        replyDto2.setMessage(DISCUSSION_REPLY)
        replyDto2.setUserId(teacher.getId())
        replyDto2.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto2.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto2)
        replyDto2.setId(replyRepository.findAll().get(1).getId())
        discussionService.changeReplyAvailability(replyDto.getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto2.getId())

        then: "the availability has changed"
        replyRepository.count() == 2L
        def result = replyRepository.findById(replyDto2.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isAvailable() == true
        def resultDiscussion = discussionRepository.findById(discussionDto.getId()).get()
        resultDiscussion.available == true
    }

    def "changes reply to not available with discussion available and no public replies"(){
        given: "an available discussion and available reply"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
        //turns discussion to available
        discussionService.changeReplyAvailability(replyDto.getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto.getId())

        then: "the availability has changed"
        replyRepository.count() == 1L
        def result = replyRepository.findById(replyDto.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isAvailable() == false
        def resultDiscussion = discussionRepository.findById(discussionDto.getId()).get()
        resultDiscussion.available == false
    }

    def "changes reply to not available with discussion available and public replies"(){
        given: "an available discussion and available reply"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
        //turns discussion to available
        discussionService.changeReplyAvailability(replyDto.getId())

        and: "a non available reply"
        replyDto2 = new ReplyDto()
        replyDto2.setMessage(DISCUSSION_REPLY)
        replyDto2.setUserId(teacher.getId())
        replyDto2.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto2.setAvailable(false)
        discussionService.createReply(discussionDto, replyDto2)
        replyDto2.setId(replyRepository.findAll().get(1).getId())
        discussionService.changeReplyAvailability(replyDto2.getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto.getId())

        then: "the availability has changed"
        replyRepository.count() == 2L
        def result = replyRepository.findById(replyDto.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isAvailable() == false
        def resultDiscussion = discussionRepository.findById(discussionDto.getId()).get()
        resultDiscussion.available == true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
