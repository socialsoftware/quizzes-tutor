package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Shared

@DataJpaTest
class GetUserDiscussionsTest extends SpockTest {

    @Shared
    def student
    @Shared
    def question1
    def quiz
    def quizAnswer

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, false)
        userRepository.save(student)

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
    }

    def "get created discussion"(){
        given:"a discussion dto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        and: "a student"
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())
        and: "created discussion"
        discussionService.createDiscussion(discussionDto)

        when:
        def discussionsResult = discussionService.findDiscussionsByUserId(student.getId())

        then: "the correct discussion is retrieved"
        discussionsResult.size() == 1
        def discussion = discussionsResult.get(0)
        discussion.getUserId() == discussionDto.getUserId()
        discussion.getQuestionId() == discussionDto.getQuestionId()
        discussion.getMessage() == discussionDto.getMessage()

    }

    def "get discussion of invalid question"(){
        given: "an invalid question id"
        def userId = -3

        when: "getting question discussion"
        def discussions = discussionService.findDiscussionsByUserId(userId);

        then:
        discussions.size() == 0
    }

    def "get discussion from user with no discussion"(){
        given:"a discussion dto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        and: "a student"
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())

        when:
        def discussionsResult = discussionService.findDiscussionsByUserId(student.getId())

        then: "no discussion is retrieved"
        discussionsResult.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}