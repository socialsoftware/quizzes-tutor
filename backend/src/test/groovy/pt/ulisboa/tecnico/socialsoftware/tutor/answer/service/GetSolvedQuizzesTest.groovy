package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

@DataJpaTest
class GetSolvedQuizzesTest extends SpockTest {
    def user
    def courseDto
    def question
    def option
    def quiz
    def quizQuestion

    def setup() {
        createExternalCourseAndExecution()

        courseDto = new CourseExecutionDto(externalCourseExecution)

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setContent("Question Content")
        question.setTitle("Question Title")
        questionRepository.save(question)

        def questionDetails = new MultipleChoiceQuestion();
        question.setQuestionDetails(questionDetails);
        questionDetailsRepository.save(questionDetails)

        option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
    }

    @Unroll
    def "returns solved quiz with: quizType=#quizType | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quiz answered by the user'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quiz.setCourseExecution(externalCourseExecution)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, option);
        questionAnswer.setAnswerDetails(answerDetails);

        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        def solvedQuizDtos = answerService.getSolvedQuizzes(user.getId(), courseDto.getCourseExecutionId())

        then: 'returns correct data'
        solvedQuizDtos.size() == 1
        def solvedQuizDto = solvedQuizDtos.get(0)
        def statementQuizDto = solvedQuizDto.getStatementQuiz()
        statementQuizDto.getQuestions().size() == 1
        solvedQuizDto.statementQuiz.getAnswers().size() == 1
        def answer = solvedQuizDto.statementQuiz.getAnswers().get(0)
        answer.getSequence() == 0
        answer.getAnswerDetails().getOptionId() == option.getId()
        solvedQuizDto.getCorrectAnswers().size() == 1
        def correct = solvedQuizDto.getCorrectAnswers().get(0)
        correct.getSequence() == 0
        correct.getCorrectAnswerDetails().getCorrectOptionId() == option.getId()

        where:
        quizType                 | conclusionDate    | resultsDate
        Quiz.QuizType.GENERATED  | null              | null
        Quiz.QuizType.PROPOSED   | null              | null
        Quiz.QuizType.IN_CLASS   | LOCAL_DATE_BEFORE | LOCAL_DATE_YESTERDAY
        Quiz.QuizType.IN_CLASS   | LOCAL_DATE_BEFORE | null
    }

    @Unroll
    def "does not return quiz with: quizType=#quizType | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quiz answered by the user'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quiz.setCourseExecution(externalCourseExecution)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, option);
        questionAnswer.setAnswerDetails(answerDetails)

        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)


        when:
        def solvedQuizDtos = answerService.getSolvedQuizzes(user.getId(), courseDto.getCourseExecutionId())

        then: 'returns no quizzes'
        solvedQuizDtos.size() == 0

        where:
        quizType                | conclusionDate      | resultsDate
        Quiz.QuizType.IN_CLASS  | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER
        Quiz.QuizType.IN_CLASS  | LOCAL_DATE_TOMORROW | null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
