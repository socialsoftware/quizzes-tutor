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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateReplyTest extends SpockTest {

    def student
    def student2
    def teacher
    def questionAnswer
    def quizAnswer
    def question1
    def discussion
    def courseExecution
    def createdQuiz

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student)

        student2 = new User(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student2)

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
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUserName(student.getUsername())
        discussionService.createDiscussion(student.getId(),questionAnswer.getId(), discussionDto)
        discussion = discussionRepository.findAll().get(0)
    }

    def "teacher replies to discussion"(){
        given: "a reply"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "a reply is given"
        discussionService.addReply(teacher.getId(), discussion.getId(), replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == teacher.getId()
        discussion.getReplies().size() == 1
        discussion.getReplies().get(0).getMessage() == replyDto.getMessage()
        discussion.getReplies().get(0).getUser().getId() == teacher.getId()
    }

    def "student replies to his discussion"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student creates a reply"
        discussionService.addReply(student.getId(), discussion.getId(), replyDto)

        then: "the correct reply was given"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getMessage() == DISCUSSION_REPLY
        result.getUser().getId() == student.getId()
        discussion.getReplies().size() == 1
        discussion.getReplies().get(0).getMessage() == replyDto.getMessage()
        discussion.getReplies().get(0).getUser().getId() == student.getId()
    }

    def "student can't reply a discussion of other student"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student tries to create a reply"
        discussionService.addReply(student2.getId(), discussion.getId(), replyDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.REPLY_UNAUTHORIZED_USER
    }

    @Unroll
    def "invalid arguments: message=#message"(){
        given: "a response created by a student"
        def replyDto = new ReplyDto()
        replyDto.setMessage(message)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "the student tries to create a reply"
        discussionService.addReply(student.getId(), discussion.getId(), replyDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        message            || errorMessage
        null               || ErrorMessage.REPLY_MISSING_MESSAGE
        "          "       || ErrorMessage.REPLY_MISSING_MESSAGE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}