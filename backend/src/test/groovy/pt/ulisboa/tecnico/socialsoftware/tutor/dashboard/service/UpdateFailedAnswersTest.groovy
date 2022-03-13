package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class UpdateFailedAnswersTest extends FailedAnswersSpockTest {
    def quiz
    def quizQuestion

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        quiz = createQuiz(1)
        quizQuestion = createQuestion(1, quiz)
    }

    @Unroll
    def "create failed answer answered=#answered"() {
        given:
        def questionAnswer = answerQuiz(answered, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then:
        failedAnswerRepository.count() == 1L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getId() != 0
        failedAnswer.getDashboard().id === dashboard.getId()
        failedAnswer.getQuestionAnswer().getId() == questionAnswer.getId()
        failedAnswer.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        failedAnswer.getAnswered() == answered
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))

        where:
        answered << [true, false]
    }

    @Unroll
    def "does not create failed answer with correct=#correct and completed=#completed" () {
        given:
        answerQuiz(true, correct, completed, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then:
        failedAnswerRepository.findAll().size() == 0L

        where:
        completed | correct
        false     | false
        false     | true
        true      | true
    }

    def "does not create failed answer for answer of IN_CLASS quiz where results date is later" () {
        given:
        def inClassQuiz= createQuiz(2, Quiz.QuizType.IN_CLASS.toString())
        inClassQuiz.setResultsDate(DateHandler.now().plusDays(1))
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, inClassQuiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then:
        failedAnswerRepository.findAll().size() == 0L
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getLastCheckFailedAnswers().isEqual(questionAnswer.getQuizAnswer().getCreationDate().minusSeconds(1))
    }

    def "create failed answer for answer of IN_CLASS quiz where results date is now" () {
        given:
        def inClassQuiz= createQuiz(2, Quiz.QuizType.IN_CLASS.toString())
        inClassQuiz.setResultsDate(DateHandler.now())
        answerQuiz(true, false, true, quizQuestion, inClassQuiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then:
        failedAnswerRepository.findAll().size() == 1L
    }

    def "updates failed answers after last check"() {
        given:
        dashboard.setLastCheckFailedAnswers(LocalDateTime.now().minusDays(1))
        answerQuiz(true, false, true, quizQuestion, quiz, LocalDateTime.now().minusDays(2))
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then:
        failedAnswerRepository.count() == 1L
        def questionAnswers = failedAnswerRepository.findAll()*.questionAnswer
        questionAnswers.contains(questionAnswer2)
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 1
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    @Unroll
    def "updates failed answers in specific time period adding seconds=#inSeconds do start date"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, LOCAL_DATE_BEFORE.plusSeconds(inSeconds))

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(),  DateHandler.toISOString(LOCAL_DATE_BEFORE),  DateHandler.toISOString(LOCAL_DATE_YESTERDAY))

        then:
        failedAnswerRepository.count() == 1L
        def questionAnswers = failedAnswerRepository.findAll()*.questionAnswer
        questionAnswers.contains(questionAnswer)
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 1
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))

        where:
        inSeconds << [1, 60*60*24.intdiv(2), 60*60*24]
    }

    @Unroll
    def "does not create failed answers in specific time period adding seconds=#inSeconds do start date"() {
        given:
       answerQuiz(true, false, true, quizQuestion, quiz, LOCAL_DATE_BEFORE.plusSeconds(inSeconds))

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(),  DateHandler.toISOString(LOCAL_DATE_BEFORE),  DateHandler.toISOString(LOCAL_DATE_YESTERDAY))

        then:
        failedAnswerRepository.count() == 0L
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 0

        where:
        inSeconds << [-20, 0, 60*60*24+1, 60*60*24*2]
    }

    def "does not create the same failed answer twice"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, LOCAL_DATE_BEFORE.plusSeconds(20))
        def failedAnswer = createFailedAnswer(questionAnswer, LocalDateTime.now())

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(),  DateHandler.toISOString(LOCAL_DATE_BEFORE),  DateHandler.toISOString(LOCAL_DATE_YESTERDAY))

        then:
        failedAnswerRepository.count() == 1L
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 1
        dashboard.getFailedAnswers().contains(failedAnswer)
    }

    def "does not create failed answers if answered other student"() {
        given:
        def otherStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        otherStudent.addCourse(externalCourseExecution)
        userRepository.save(otherStudent)
        and:
        dashboard.setStudent(otherStudent)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L
        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === otherStudent.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    def "does not create failed answers if quiz does not belong to the course execution"() {
        given:
        def otherExternalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(otherExternalCourseExecution)
        and:
        dashboard.setCourseExecution(otherExternalCourseExecution)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId(), null, null)

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L
        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === otherExternalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    @Unroll
    def "cannot update failed answers with dashboardId=#dashboardId"() {
        when:
        failedAnswerService.updateFailedAnswers(dashboardId, null, null)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
        and:
        failedAnswerRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
