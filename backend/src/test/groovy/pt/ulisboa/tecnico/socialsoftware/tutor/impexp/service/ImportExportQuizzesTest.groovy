package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto

@DataJpaTest
class ImportExportQuizzesTest extends SpockTest {
    def quiz
    def creationDate
    def availableDate
    def conclusionDate

    def setup() {
        createExternalCourseAndExecution()

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto()
        optionDto.setSequence(1)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto = questionService.createQuestion(externalCourse.getId(), questionDto)


        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(false)
        quizDto.setQrCodeOnly(true)
        quizDto.setOneWay(false)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = DateHandler.now()
        availableDate = DateHandler.now()
        conclusionDate = DateHandler.now().plusDays(2)
        quizDto.setCreationDate(DateHandler.toISOString(creationDate))
        quizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        quizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        quizDto.setType(Quiz.QuizType.EXAM.toString())
        quiz = quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        quizService.addQuestionToQuiz(questionDto.getId(), quiz.getId())
    }

    def 'export and import quizzes'() {
        given: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzesToXml()
        and: 'delete quiz and quizQuestion'
        print quizzesXml
        quizService.removeQuiz(quiz.getId())

        when:
        quizService.importQuizzesFromXml(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 1
        def quizResult = quizRepository.findAll().get(0)
        quizResult.getKey() == 1
        !quizResult.getScramble()
        quizResult.isQrCodeOnly()
        !quizResult.isOneWay()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getType() == Quiz.QuizType.EXAM
        quizResult.getQuizQuestionsNumber() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getKey() == 1
    }

    def 'export quiz to latex'() {
        when:
        def quizzesLatex = quizService.exportQuizzesToLatex(quiz.getId())

        then:
        quizzesLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
