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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared
import spock.lang.Unroll

@DataJpaTest
class CreateDiscussionTest extends SpockTest {

    @Shared
    def student
    @Shared
    def question1
    def question2
    def quiz
    def quizAnswer

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, AuthUser.Type.TECNICO)
        userRepository.save(student)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setTitle("Question title")
        question1.setContent("Question Content")
        questionRepository.save(question1)

        question2 = new Question()
        question2.setCourse(externalCourse)
        question2.setTitle("Question title")
        question2.setContent("Question Content")
        questionRepository.save(question2)

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

    def "create discussion"(){
        given:"a discussion dto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        and: "a student"
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())

        when:
        discussionService.createDiscussion(discussionDto)

        then: "the correct discussion is inside the repository"
        discussionRepository.count() == 1L

        def resultRepo = discussionService.findDiscussionByUserIdAndQuestionId(student.getId(), question1.getId())
        resultRepo.size() == 1
        def resultQuestion2 = resultRepo.get(0)
        resultQuestion2.getMessage() == DISCUSSION_MESSAGE
        resultQuestion2.getUserId() == student.getId()
        resultQuestion2.getQuestion().getId() == question1.getId()

        def resultUserList = discussionService.findDiscussionsByUserId(student.getId())
        resultUserList.size() == 1
        def resultUser = resultUserList.get(0)
        resultUser.getMessage() == DISCUSSION_MESSAGE
        resultUser.getUserId() == student.getId()
        resultUser.getQuestion().getId() == question1.getId()

        def resultDiscussionList = discussionService.findDiscussionsByQuestionId(question1.getId())
        resultDiscussionList.size() == 1
        def resultQuestion = resultDiscussionList.get(0)
        resultQuestion.getMessage() == DISCUSSION_MESSAGE
        resultQuestion.getUserId() == student.getId()
        resultQuestion.getQuestion().getId() == question1.getId()
    }

    def "student can't create a discussion in a non answered question"(){
        given: "a discussionDto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setUserId(student.getId())
        discussionDto.setQuestion(new QuestionDto(question2))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "creating a discussion on a non answered question"
        discussionService.createDiscussion(discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_ANSWERED
    }


    def "user can't create two discussions for the same question"(){
        given: "a student"
        def studentId = student.getId()

        and: "a discussion dto"
        def discussionDto1 = new DiscussionDto()
        discussionDto1.setMessage(DISCUSSION_MESSAGE)
        discussionDto1.setUserId(studentId)
        discussionDto1.setQuestion(new QuestionDto(question1))
        discussionDto1.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto1.setUserName(student.getUsername())

        and: "another discussion dto"
        def discussionDto2 = new DiscussionDto()
        discussionDto2.setMessage(DISCUSSION_MESSAGE)
        discussionDto2.setUserId(studentId)
        discussionDto2.setQuestion(new QuestionDto(question1))
        discussionDto2.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto2.setUserName(student.getUsername())

        when: "creating two discussions on the same question"
        discussionService.createDiscussion(discussionDto1)
        discussionService.createDiscussion(discussionDto2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DUPLICATE_DISCUSSION
    }

    @Unroll
    def "invalid arguments: question=#question | userId=#userId | content=#content | date=#date"(){
        given: "a discusssionDto"
        def discussionDto = new DiscussionDto()
        discussionDto.setQuestion(question)
        discussionDto.setUserId(userId)
        discussionDto.setMessage(content)
        discussionDto.setDate(date)
        userRepository.save(student)

        when: "creating discussion"
        discussionService.createDiscussion(discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        question                   | userId          | content            | date                                      || errorMessage
        null                       | student.getId() | DISCUSSION_MESSAGE | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_QUESTION
        new QuestionDto(question1) | null            | DISCUSSION_MESSAGE | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_USER
        new QuestionDto(question1) | student.getId() | null               | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE
        new QuestionDto(question1) | student.getId() | "          "       | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}