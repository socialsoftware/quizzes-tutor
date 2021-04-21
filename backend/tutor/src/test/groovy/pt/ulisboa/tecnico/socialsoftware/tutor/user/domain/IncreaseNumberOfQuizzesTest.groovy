package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import spock.lang.Unroll

@DataJpaTest
class IncreaseNumberOfQuizzesTest extends SpockTest{
    def user
    def quiz

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, false)
        userRepository.save(user)

        QuizDto quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle(QUIZ_TITLE)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)

        Question question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle(QUESTION_1_TITLE)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        Option option = new Option()
        option.setSequence(1)
        option.setCorrect(true)
        question.getQuestionDetails().addOption(option)

        QuestionDto questionDto = question.getDto()
        questionDto.setKey(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quizDto.setQuestions(questions)

        quiz = new Quiz(quizDto)
        quiz.setCourseExecution(externalCourseExecution);
        quizRepository.save(quiz)

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, 1)

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)

        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1, 1)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))
    }

    @Unroll
    def "increase number quizzes" () {
        when:
        user.increaseNumberOfQuizzes(type)

        then:
        user.getNumberOfTeacherQuizzes() == nTeacherQuizzes
        user.getNumberOfStudentQuizzes() == nStudentQuizzes
        user.getNumberOfInClassQuizzes() == nInClassQuizzes

        where:
        type                    || nTeacherQuizzes  | nStudentQuizzes | nInClassQuizzes
        QuizType.PROPOSED  || 1                | 0               | 0
        QuizType.GENERATED || 0                | 1               | 0
        QuizType.IN_CLASS  || 0                | 0               | 1
        QuizType.TEST      || 0                | 0               | 0
        QuizType.EXAM      || 0                | 0               | 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
