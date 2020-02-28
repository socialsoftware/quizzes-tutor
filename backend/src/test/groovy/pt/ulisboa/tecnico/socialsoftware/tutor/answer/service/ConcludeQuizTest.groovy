package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_AVAILABLE

@DataJpaTest
class ConcludeQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    AnswerService answerService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def user
    def courseExecution
    def quizQuestion
    def optionOk
    def optionKO
    def quizAnswer
    def date
    def quiz

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)


        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        course.addQuestion(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        optionKO = new Option()
        optionKO.setCorrect(false)
        question.addOption(optionKO)
        optionOk = new Option()
        optionOk.setCorrect(true)
        question.addOption(optionOk)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(user, quiz)
        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
        optionRepository.save(optionOk)
        optionRepository.save(optionKO)
    }

    def 'conclude quiz without conclusionDate, without answering'() {
        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() != null
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == null
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz IN_CLASS without answering, before conclusionDate'() {
        given: 'an IN_CLASS quiz with future conclusionDate'
        quiz.setConclusionDate(LocalDateTime.now().plusDays(2));
        quiz.setType(Quiz.QuizType.IN_CLASS);

        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() != null
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == null
        and: 'does not return answers'
        correctAnswers == []
    }

    def 'conclude quiz with answer, before conclusionDate'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(LocalDateTime.now().plusDays(2))
        and: 'an answer'
        def statementAnswerDto = new StatementAnswerDto();
        statementAnswerDto.setOptionId(optionOk.getId())
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        answerService.submitAnswer(user, quiz.getId(), statementAnswerDto);

        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == optionOk
        optionOk.getQuestionAnswers().contains(questionAnswer)
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz without answering, before availableDate'() {
        given: 'a quiz with future availableDate'
        quiz.setAvailableDate(LocalDateTime.now().plusDays(2))

        when:
        answerService.concludeQuiz(user, quiz.getId())

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NOT_YET_AVAILABLE
    }

    def 'user not consistent'() {
        given: 'another user'
        def otherUser = new User('name', "username2", 2, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(otherUser)

        when:
        answerService.concludeQuiz(otherUser, quiz.getId())

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NOT_FOUND
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}
