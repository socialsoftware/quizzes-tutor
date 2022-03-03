package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler


import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_CREATE_DIFFICULT_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND

@DataJpaTest
class CreateDifficultQuestionTest extends SpockTest {
    def student
    def dashboard
    def question
    def optionOK
    def optionKO

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, false)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    @Unroll
    def "create difficult question with percentage #percentage"() {
        when: "a weekly score is created"
        difficultQuestionService.createDifficultQuestions(dashboard.getId(), question.getId(), percentage)

        then: "the weekly score is inside the weekly score repository and with the correct data"
        difficultQuestionRepository.count() == 1L
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getQuestion().getId() == question.getId()
        result.isRemoved() == false
        result.getRemovedDate() == null
        result.getPercentage() == percentage
        and: "dashboard contains the difficult question"
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getDifficultQuestions().contains(result)
        dashboard.getLastCheckDifficultQuestions().isAfter(DateHandler.now().minusMinutes(1))

        where:
        percentage << [0, 12, 24]
    }

    def "cannot create two difficult questions for the same question"() {
        given: "a difficult question"
        difficultQuestionService.createDifficultQuestions(dashboard.getId(), question.getId(), 13)

        when: "when it is created a new difficult question for the same question"
        difficultQuestionService.createDifficultQuestions(dashboard.getId(), question.getId(), 24)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DIFFICULT_QUESTION_ALREADY_CREATED
        and: "there is a difficult question in the database"
        dashboardRepository.count() == 1L
    }

    @Unroll
    def "cannot create difficult question with percentage=#percentage"() {
        when: "a weekly score is created"
        difficultQuestionService.createDifficultQuestions(dashboard.getId(), question.getId(), percentage)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_CREATE_DIFFICULT_QUESTION
        weeklyScoreRepository.count() == 0L

        where:
        percentage << [-100, -1, 25, 50, 150]

    }

    @Unroll
    def "cannot create difficult question with dashboardId=#dashboardId"() {
        when: "a weekly score is created"
        difficultQuestionService.createDifficultQuestions(dashboardId, question.getId(), 20)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND
        weeklyScoreRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }

    @Unroll
    def "cannot create difficult question with questionId=#questionId"() {
        when: "a weekly score is created"
        difficultQuestionService.createDifficultQuestions(dashboard.getId(), questionId, 20)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUESTION_NOT_FOUND
        weeklyScoreRepository.count() == 0L

        where:
        questionId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}