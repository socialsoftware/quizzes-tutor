package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

@DataJpaTest
class FindQuestionsTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
    }

    def "create a question with image and two options and a quiz questions with two answers"() {
        given: "createQuestion a question"
        def question = new Question()
        question.setKey(1)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(course)
        and: 'an image'
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)
        imageRepository.save(image)
        question.setImage(image)
        and: 'two options'
        def optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        question.addOption(optionOK)
        def optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)
        question.addOption(optionKO)
        questionRepository.save(question)

        def quiz = new Quiz()
        quiz.setType(Quiz.QuizType.PROPOSED)
        quiz.setKey(1)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        question.addQuizQuestion(quizQuestion)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestion.setQuiz(quiz)
        quizRepository.save(quiz);
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.addQuestionAnswer()
        quizAnswerRepository.save(quizAnswer)
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setOption(optionOK)
        questionAnswerRepository.save(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer = new QuestionAnswer()
        questionAnswer.setOption(optionKO)
        questionAnswerRepository.save(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)

        when:
        def result = questionService.findQuestions(course.getId())

        then: "the returned data are correct"
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getStatus() == Question.Status.AVAILABLE.name()
        resQuestion.getContent() == QUESTION_CONTENT
        resQuestion.getNumberOfAnswers() == 2
        resQuestion.getNumberOfCorrect() == 1
        resQuestion.getDifficulty() == 50
        resQuestion.getImage().getId() != null
        resQuestion.getImage().getUrl() == URL
        resQuestion.getImage().getWidth() == 20
        resQuestion.getOptions().size() == 2
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
