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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared

@DataJpaTest
class ChangeReplyAvailabilityTest extends SpockTest {

    def student
    def teacher
    def question1
    def questionAnswer
    def quizAnswer
    def discussion
    def courseExecution
    def replyDto
    def createdQuiz

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student)

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        courseExecution = courseExecutionRepository.findAll().get(0)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setContent(QUESTION_1_CONTENT)

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)

        question1.setOptions(options)
        questionRepository.save(question1)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        createdQuiz = quizRepository.findAll().get(0)

        def quizanswer = new QuizAnswer()
        quizanswer.setUser(student)
        quizanswer.setQuiz(quiz)
        quizanswer.setQuiz(createdQuiz)
        quizAnswerRepository.save(quizanswer)
        quizAnswer = quizAnswerRepository.findAll().get(0)

        def quizquestion = new QuizQuestion(createdQuiz, question1, 0)
        quizQuestionRepository.save(quizquestion)

        def questionanswer = new QuestionAnswer()
        questionanswer.setTimeTaken(1)
        questionanswer.setQuizAnswer(quizAnswer)
        questionanswer.setQuizQuestion(quizquestion)
        questionanswer.setOption(optionRepository.findAll().get(0))
        questionAnswerRepository.save(questionanswer)
        questionAnswer = questionAnswerRepository.findAll().get(0)
        quizquestion.addQuestionAnswer(questionAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)

        quiz.addQuizAnswer(quizAnswer)
        quiz.addQuizQuestion(quizQuestionRepository.findAll().get(0))
        quiz.setCourseExecution(courseExecution)

        student.addQuizAnswer(quizAnswer)

        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())
        discussionService.createDiscussion(questionAnswer.getId(), discussionDto)
        discussion = discussionRepository.findAll().get(0)
    }

    def "changes reply to available"(){
        given: "a not public reply"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setPublic(false)
        discussionService.addReply(discussion.getId(), replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto.getId())

        then: "reply is public"
        replyRepository.count() == 1L
        def result = replyRepository.findById(replyDto.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isPublic() == true
    }

    def "changes reply to not available"(){
        given: "a public reply"
        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setPublic(false)
        discussionService.addReply(discussion.getId(), replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
        discussionService.changeReplyAvailability(replyDto.getId())

        when: "change reply availability"
        discussionService.changeReplyAvailability(replyDto.getId())

        then: "reply is not public"
        replyRepository.count() == 1L
        def result = replyRepository.findById(replyDto.getId()).get()
        result.getMessage() == DISCUSSION_REPLY
        result.getUser() == teacher
        result.isPublic() == false
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
