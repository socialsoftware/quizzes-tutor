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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
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
    def quizAnswer
    def questionAnswer
    def questionAnswer2
    def courseExecution
    def createdQuiz

    def setup(){
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)
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
        questionanswer2.setTimeTaken(0)
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
    }

    def "create discussion"(){
        given:"a discussion dto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setQuestion(new QuestionDto(question1))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUserId(student.getId())
        discussionDto.setUserName(student.getUsername())

        when: "discussion is created"
        discussionService.createDiscussion(questionAnswer.getId(), discussionDto)

        then: "the correct discussion is inside the repository"
        discussionRepository.count() == 1L
        def resultUserList = discussionService.findByCourseExecutionIdAndUserId(courseExecution.getId(),student.getId())
        resultUserList.size() == 1
        def resultUser = resultUserList.get(0)
        resultUser.getMessage() == DISCUSSION_MESSAGE
        resultUser.getUserId() == student.getId()
        resultUser.getQuestion().getId() == question1.getId()
    }

    def "student can't create a discussion in a non answered question"(){
        given: "a discussionDto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setUserId(student.getId())
        discussionDto.setQuestion(new QuestionDto(question2))
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when: "creating a discussion on a non answered question"
        discussionService.createDiscussion(questionAnswer2.getId(), discussionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_ANSWERED
    }


   def "user can't create two discussions for the same question"(){
       given: "a discussion dto"
       def discussionDto1 = new DiscussionDto()
       discussionDto1.setMessage(DISCUSSION_MESSAGE)
       discussionDto1.setUserId(student.getId())
       discussionDto1.setQuestion(new QuestionDto(question1))
       discussionDto1.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
       discussionDto1.setUserName(student.getUsername())

       and: "another discussion dto"
       def discussionDto2 = new DiscussionDto()
       discussionDto2.setMessage(DISCUSSION_MESSAGE)
       discussionDto2.setUserId(student.getId())
       discussionDto2.setQuestion(new QuestionDto(question1))
       discussionDto2.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
       discussionDto2.setUserName(student.getUsername())

       when: "creating two discussions on the same question"
       discussionService.createDiscussion(questionAnswer.getId(), discussionDto1)
       discussionService.createDiscussion(questionAnswer.getId(), discussionDto2)

       then: "exception is thrown"
       def exception = thrown(TutorException)
       exception.getErrorMessage() == ErrorMessage.DUPLICATE_DISCUSSION
   }

    /*@Unroll
    def "invalid arguments: question=#question | userId=#userId | message=#message | date=#date"(){
        given: "a discusssionDto"
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(message)
        discussionDto.setUserId(userId)
        discussionDto.setQuestion(question)
        discussionDto.setDate(date)

        when: "creating discussion"
        discussionService.createDiscussion(questionAnswer.getId(), discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        question                   | userId          | message          | date                                      || errorMessage
        null                       | student.getId() | DISCUSSION_MESSAGE | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_QUESTION
        new QuestionDto(question1) | null            | DISCUSSION_MESSAGE | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_USER
        new QuestionDto(question1) | student.getId() | null               | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE
        new QuestionDto(question1) | student.getId() | "          "       | DateHandler.toISOString(LOCAL_DATE_TODAY) || ErrorMessage.DISCUSSION_MISSING_MESSAGE
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}