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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared

@DataJpaTest
class CreateReplyTest extends SpockTest {

    @Shared
    def student
    def student2
    def teacher
    @Shared
    def question1
    def discussionDto

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student)

        student2 = new User(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student2)

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
        quizquestion.getQuestionAnswers().clear()
        quizanswer.getQuestionAnswers().clear()
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
        discussionService.createDiscussion(quizanswer.getId(), questionRepository.findAll().get(0).getId(), discussionDto)
        discussionDto.setId(discussionRepository.findAll().get(0).getId())
    }

    def "teacher replies to discussion"(){
        given: "a reply"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "a reply is given"
        discussionService.createReply(discussionDto, replyDto)

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
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student creates a reply"
        discussionService.createReply(discussionDto, replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == student
        discussionRepository.findAll().get(0).getReplies().size() == 1
        discussionRepository.findAll().get(0).getReplies().get(0).getMessage() == replyDto.getMessage()
        discussionRepository.findAll().get(0).getReplies().get(0).getUser().getId() == replyDto.getUserId()
    }

    def "student can't reply a discussion of other student"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(student2.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student tries to create a reply"
        discussionService.createReply(discussionDto, replyDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.REPLY_UNAUTHORIZED_USER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}