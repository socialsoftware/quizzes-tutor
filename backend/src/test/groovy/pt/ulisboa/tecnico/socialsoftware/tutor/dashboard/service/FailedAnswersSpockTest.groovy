package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.time.LocalDateTime

class FailedAnswersSpockTest extends SpockTest {

    def dashboard
    def student
    def optionKO

    def quiz
    def question
    def option1
    def option2

    def createQuiz(count) {
        def quiz = new Quiz()
        quiz.setKey(count)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuestion(count, quiz) {
        def question = new Question()
        question.setKey(count)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(answered, correct, completed, question, quiz, date = LocalDateTime.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(question)

        def answerDetails
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(0))
        else if (answered && !correct ) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(1))
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        questionAnswerRepository.save(questionAnswer)
        return questionAnswer
    }

    def createFailedAnswer(questionAnswer, collected) {
        def failedAnswer = new FailedAnswer()
        failedAnswer.setQuestionAnswer(questionAnswer)
        failedAnswer.setAnswered(questionAnswer.isAnswered())
        failedAnswer.setCollected(collected)
        failedAnswer.setDashboard(dashboard)
        failedAnswerRepository.save(failedAnswer)

        return failedAnswer
    }

    def createQuizAndQuestionIT(){
        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle("Quiz Title")
        quizDto.setType(Quiz.QuizType.PROPOSED.toString())
        quizDto.setAvailableDate(STRING_DATE_YESTERDAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)


        question = new QuestionDto()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setStatus(Question.Status.AVAILABLE.name())

        def questionDetails = new MultipleChoiceQuestionDto()

        option1 = new OptionDto()
        option1.setContent("Option Content")
        option1.setCorrect(true)
        option1.setSequence(0)

        option2 = new OptionDto()
        option2.setContent("Option Content")
        option2.setCorrect(false)
        option2.setSequence(1)

        def options = new ArrayList<OptionDto>()
        options.add(option1)
        options.add(option2)

        questionDetails.setOptions(options)
        question.setQuestionDetailsDto(questionDetails)

        def questionDto = questionService.createQuestion(courseExecution.getCourseExecutionId(), question)

        def questions = new ArrayList<QuestionDto>()
        questions.add(questionDto)

        quiz = quizService.createQuiz(courseExecution.getCourseExecutionId(), quizDto)
    }

    def answerQuizIT(){
        def quizAnswer = answerService.createQuizAnswer(student.getId(), quiz.getId())

        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()

        def statementAnswerDto = new StatementAnswerDto()
        def multipleChoiceAnswerDto = new MultipleChoiceStatementAnswerDetailsDto()
        multipleChoiceAnswerDto.setOptionId(option1.getId())
        multipleChoiceAnswerDto.setOptionId(option2.getId())
        statementAnswerDto.setAnswerDetails(multipleChoiceAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(question.getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        answerService.concludeQuiz(statementQuizDto)

        return statementAnswerDto
    }
}
