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
class ChangeStatusTest extends SpockTest {
    @Shared
    def student
    def teacher
    @Shared
    def question1
    def discussionDto
    def replyDto

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
        discussionService.createDiscussion(quizanswer.getId(), 0, discussionDto)
        discussionDto.setId(discussionRepository.findAll().get(0).getId())

        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionService.createReply(discussionDto, replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
    }

    def "changes discussion with replies status to closed"(){
        when: "change open discussion with replies status"
        discussionService.changeStatus(discussionDto.getId())

        then: "the status has changed"
        def resultDiscussionList = discussionService.findDiscussionsByUserId(student.getId())
        resultDiscussionList.size() == 1
        def discussion = resultDiscussionList.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE
        discussion.getUserId() == student.getId()
        discussion.getQuestion().getId() == question1.getId()
        discussion.isClosed() == true
    }

    def "changes discussion with no replies status to closed"(){
        given:
        def quiz = new Quiz()
        quiz.setKey(2)
        quiz.setType("TEST")
        quiz.setCourseExecution(courseExecutionRepository.findAll().get(0))
        quizRepository.save(quiz)

        def quizanswer = new QuizAnswer()
        quizanswer.setUser(student)
        quizanswer.setQuiz(quiz)
        quizanswer.setQuiz(quizRepository.findAll().get(1))
        quizAnswerRepository.save(quizanswer)

        def quizquestion = new QuizQuestion(quizRepository.findAll().get(1), question1, 0)
        quizQuestionRepository.save(quizquestion)

        def questionanswer = new QuestionAnswer()
        questionanswer.setTimeTaken(1)
        questionanswer.setQuizAnswer(quizanswer)
        questionanswer.setQuizQuestion(quizquestion)
        questionAnswerRepository.save(questionanswer)

        quizquestion.addQuestionAnswer(questionAnswerRepository.findAll().get(1))
        quizanswer.addQuestionAnswer(questionAnswerRepository.findAll().get(1))
        quiz.addQuizAnswer(quizAnswerRepository.findAll().get(1))
        quiz.addQuizQuestion(quizQuestionRepository.findAll().get(1))
        quiz.setCourseExecution(courseExecutionRepository.findAll().get(0))

        student.addQuizAnswer(quizAnswerRepository.findAll().get(1))

        def discussionDto2 = new DiscussionDto()
        discussionDto2.setMessage(DISCUSSION_MESSAGE)
        discussionDto2.setQuestion(new QuestionDto(question1))
        discussionDto2.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto2.setUserId(student.getId())
        discussionDto2.setUserName(student.getUsername())
        discussionService.createDiscussion(quizanswer.getId(), 0, discussionDto2)
        discussionDto2.setId(discussionRepository.findAll().get(1).getId())

        when: "change open discussion with no replies status"
        discussionService.changeStatus(discussionDto2.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLOSE_NOT_POSSIBLE
    }

    def "changes discussion with replies status twice"(){
        when: "change open discussion with replies status"
        discussionService.changeStatus(discussionDto.getId())
        discussionService.changeStatus(discussionDto.getId())

        then: "the status has changed"
        def resultDiscussionList = discussionService.findDiscussionsByUserId(student.getId())
        resultDiscussionList.size() == 1
        def discussion = resultDiscussionList.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE
        discussion.getUserId() == student.getId()
        discussion.getQuestion().getId() == question1.getId()
        discussion.isClosed() == false
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
