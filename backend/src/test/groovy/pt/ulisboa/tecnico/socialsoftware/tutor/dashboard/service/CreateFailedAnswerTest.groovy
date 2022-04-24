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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateFailedAnswerTest extends FailedAnswersSpockTest {
    def question
    def quiz
    def quizQuestion

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        question = createQuestion()
        quiz = createQuiz()
        quizQuestion = createQuizQuestion(quiz, question)
    }

    @Unroll
    def "create failed answer answered=#answered"() {
        given:
        def questionAnswer = answerQuiz(answered, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        failedAnswerRepository.count() == 1L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getQuestionAnswer().getId() == questionAnswer.getId()
        result.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result.getAnswered() == answered
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)

        where:
        answered << [true, false]
    }

    def "cannot create two failed answers with the same question"() {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())
        and:
        def quiz2 = createQuiz()
        def quizQuestion2 = createQuizQuestion(quiz2, question)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer2.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.FAILED_ANSWER_ALREADY_CREATED
        and:
        failedAnswerRepository.count() == 1
    }

    def "create two failed answers with with different questions"() {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())
        and:
        def question2 = createQuestion()
        def quiz2 = createQuiz()
        def quizQuestion2 = createQuizQuestion(quiz2, question2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer2.getId())

        then:
        failedAnswerRepository.count() == 2L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getQuestionAnswer().getId() == questionAnswer.getId()
        result.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        !result.getAnswered()
        def result2 = failedAnswerRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getQuestionAnswer().getId() == questionAnswer2.getId()
        result2.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result2.getAnswered()
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)
    }

    def "cannot create two failed answer for the same question answer"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        and:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.FAILED_ANSWER_ALREADY_CREATED
        and:
        failedAnswerRepository.count() == 1
    }

    def "cannot create a failed answer that does not belong to the course execution"() {
        given:
        def otherExternalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(otherExternalCourseExecution)
        and:
        dashboard.setCourseExecution(otherExternalCourseExecution)
        and:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CREATE_FAILED_ANSWER
        and:
        failedAnswerRepository.count() == 0
    }

    def "cannot create a failed answer that was not answered by the student"() {
        given:
        def otherStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        otherStudent.addCourse(externalCourseExecution)
        userRepository.save(otherStudent)
        and:
        dashboard.setStudent(otherStudent)
        and:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CREATE_FAILED_ANSWER
        and:
        failedAnswerRepository.count() == 0
    }

    @Unroll
    def "cannot create failed answer with invalid correct=#correct or completed=#completed"() {
        given:
        def questionAnswer = answerQuiz(true, correct, completed, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CREATE_FAILED_ANSWER
        and:
        failedAnswerRepository.count() == 0

        where:
        correct | completed
        false   | false
        true    | true
    }

    @Unroll
    def "cannot create failed answer with invalid dashboardId=#dashboardId"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboardId, questionAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
        and:
        failedAnswerRepository.count() == 0

        where:
        dashboardId << [0, 100]
    }

    @Unroll
    def "cannot create failed answer with invalid questionAnswerId=#questionAnswerId"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswerId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_ANSWER_NOT_FOUND
        and:
        failedAnswerRepository.count() == 0

        where:
        questionAnswerId << [0, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}