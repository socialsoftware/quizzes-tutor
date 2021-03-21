package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion

@DataJpaTest
class AddQuestionToQuizTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()

        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        Question question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle("Question title")
        questionRepository.save(question)
    }

    def 'add a question to a quiz' () {
        given:
        def quizId = quizRepository.findAll().get(0).getId()
        def questionId = questionRepository.findAll().get(0).getId()

        when:
        quizService.addQuestionToQuiz(questionId, quizId)

        then:
        quizQuestionRepository.findAll().size() == 1
        QuizQuestion quizQuestion = quizQuestionRepository.findAll().get(0)
        quizQuestion.getId() != null
        quizQuestion.getSequence() == 0
        quizQuestion.getQuiz() != null
        quizQuestion.getQuiz().getQuizQuestionsNumber() == 1
        quizQuestion.getQuiz().getQuizQuestions().contains(quizQuestion)
        quizQuestion.getQuestion() != null
        quizQuestion.getQuestion().getQuizQuestions().size() == 1
        quizQuestion.getQuestion().getQuizQuestions().contains(quizQuestion)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
