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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared

@DataJpaTest
class ChangeStatusTest extends SpockTest {

    def student
    def teacher
    def question1
    def question2
    def questionAnswer
    def questionAnswer2
    def quizAnswer
    def discussion
    def courseExecution
    def replyDto
    def createdQuiz

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)

        teacher = new User(USER_2_NAME,USER_2_USERNAME, USER_1_EMAIL, User.Role.TEACHER, true, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        courseExecution = courseExecutionRepository.findAll().get(0)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setContent(QUESTION_1_CONTENT)

        question2 = new Question()
        question2.setCourse(externalCourse)
        question2.setTitle(QUESTION_2_TITLE)
        question2.setContent(QUESTION_2_CONTENT)

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        options.add(optionDto2)

        question1.setOptions(options)
        questionRepository.save(question1)

        question2.setOptions(options)
        questionRepository.save(question2)

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
        def quizquestion2 = new QuizQuestion(createdQuiz, question2, 1)
        quizQuestionRepository.save(quizquestion)
        quizQuestionRepository.save(quizquestion2)

        def questionanswer = new QuestionAnswer()
        questionanswer.setTimeTaken(1)
        questionanswer.setQuizAnswer(quizAnswer)
        questionanswer.setQuizQuestion(quizquestion)
        questionanswer.setOption(optionRepository.findAll().get(0))
        questionAnswerRepository.save(questionanswer)
        questionAnswer = questionAnswerRepository.findAll().get(0)
        quizquestion.addQuestionAnswer(questionAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)

        def questionanswer2 = new QuestionAnswer()
        questionanswer2.setTimeTaken(1)
        questionanswer2.setQuizAnswer(quizAnswer)
        questionanswer2.setQuizQuestion(quizquestion)
        questionanswer2.setOption(optionRepository.findAll().get(0))
        questionAnswerRepository.save(questionanswer2)
        questionAnswer2 = questionAnswerRepository.findAll().get(1)
        quizquestion.addQuestionAnswer(questionAnswer2)
        quizAnswer.addQuestionAnswer(questionAnswer2)

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

        replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setUserId(teacher.getId())
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionService.addReply(discussion.getId(), replyDto)
        replyDto.setId(replyRepository.findAll().get(0).getId())
    }

    def "changes discussion with replies status to closed"(){
        when: "change open discussion with replies status"
        discussionService.changeStatus(discussion.getId())

        then: "the status has changed"
        def resultDiscussionList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
        resultDiscussionList.size() == 1
        def discussion = resultDiscussionList.get(0)
        discussion.getMessage() == DISCUSSION_MESSAGE
        discussion.getUserId() == student.getId()
        discussion.getQuestion().getId() == question1.getId()
        discussion.isClosed() == true
    }

    def "changes discussion with no replies status to closed"(){
        given: "a discussion with no replies"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question2))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())
        discussionService.createDiscussion(questionAnswer2.getId(), discussionDto)

        when: "change open discussion status"
        discussionService.changeStatus(discussionRepository.findAll().get(1).getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLOSE_NOT_POSSIBLE
    }

    def "changes discussion with replies status twice"(){
        when: "change open discussion with replies status"
        discussionService.changeStatus(discussion.getId())
        discussionService.changeStatus(discussion.getId())

        then: "the status remains the same"
        def resultDiscussionList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
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
