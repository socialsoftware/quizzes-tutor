package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Shared

import java.time.LocalDateTime

@DataJpaTest
class GiveReplyTest extends SpockTest {

    @Shared
    def student
    def teacher
    @Shared
    def question1
    def quiz
    def quizAnswer
    def discussionDto

    def setup(){
        student = new User(USER_1_NAME,USER_1_USERNAME, User.Role.STUDENT)
        userRepository.save(student)

        teacher = new User(USER_2_NAME,USER_2_USERNAME, User.Role.TEACHER)
        userRepository.save(teacher)

        question1 = new Question()
        question1.setCourse(course)
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

        def quizquestion = new QuizQuestion(quizRepository.findAll().get(0), question1, 3)
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
        discussionService.createDiscussion(discussionDto)
    }

    def "teacher replies to discussion"(){
        given: "a reply"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(LocalDateTime.now())


        when: "a reply is given"
        discussionService.giveReply(discussionDto, replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        discussionRepository.findAll().get(0).getReplies().size() == 1
        discussionRepository.findAll().get(0).getReplies().get(0).getMessage() == replyDto.getMessage()
        discussionRepository.findAll().get(0).getReplies().get(0).getUser().getId() == replyDto.getUserId()
    }

    def "student replies to his discussion"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(student.getId())
        replyDto.setDate(LocalDateTime.now())

        when: "the student creates a reply"
        discussionService.giveReply(discussionDto, replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == student
        discussionRepository.findAll().get(0).getReplies().size() == 1
        discussionRepository.findAll().get(0).getReplies().get(0).getMessage() == replyDto.getMessage()
        discussionRepository.findAll().get(0).getReplies().get(0).getUser().getId() == replyDto.getUserId()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}