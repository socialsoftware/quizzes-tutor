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
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer.getId())

        then:
        failedAnswerRepository.count() == 1L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getQuestionAnswer().getId() == questionAnswer.getId()
        result.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result.getAnswered() == answered
        result.getSameQuestion().getFailedAnswers().size() == 0
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)
        and:
        sameQuestionRepository.findAll().size() == 1

        where:
        answered << [true, false]
    }

    def "create two failed answers with the same question"() {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = addExistingQuestionToQuiz(quiz2)
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
        result.getSameQuestion().getFailedAnswers().size() == 1
        def result2 = failedAnswerRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getQuestionAnswer().getId() == questionAnswer2.getId()
        result2.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result2.getAnswered()
        result2.getSameQuestion().getFailedAnswers().size() == 1
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)
        and:
        sameQuestionRepository.findAll().size() == 2
        def sameQuestion1 = sameQuestionRepository.findAll().get(0)
        sameQuestion1.getFailedAnswers().size() == 1
        sameQuestion1.getFailedAnswers().contains(result2)
        def sameQuestion2 = sameQuestionRepository.findAll().get(1)
        sameQuestion2.getFailedAnswers().size() == 1
        sameQuestion2.getFailedAnswers().contains(result)
    }

    def "create three failed answers with the same question"() {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = addExistingQuestionToQuiz(quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)
        createFailedAnswer(questionAnswer2, DateHandler.now())
        and:
        def quiz3 = createQuiz(3)
        def quizQuestion3 = addExistingQuestionToQuiz(quiz3)
        def questionAnswer3 = answerQuiz(true, false, true, quizQuestion3, quiz3)

        when:
        failedAnswerService.createFailedAnswer(dashboard.getId(), questionAnswer3.getId())

        then:
        failedAnswerRepository.count() == 3L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getQuestionAnswer().getId() == questionAnswer.getId()
        result.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        !result.getAnswered()
        result.getSameQuestion().getFailedAnswers().size() == 2
        def result2 = failedAnswerRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getQuestionAnswer().getId() == questionAnswer2.getId()
        result2.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result2.getAnswered()
        result2.getSameQuestion().getFailedAnswers().size() == 2
        and:
        def result3 = failedAnswerRepository.findAll().get(2)
        result3.getId() != null
        result3.getDashboard().getId() == dashboard.getId()
        result3.getQuestionAnswer().getId() == questionAnswer3.getId()
        result3.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result3.getAnswered()
        result3.getSameQuestion().getFailedAnswers().size() == 2
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)
        dashboard.getFailedAnswers().contains(result2)
        dashboard.getFailedAnswers().contains(result3)
        and:
        sameQuestionRepository.findAll().size() == 3
        def sameQuestion1 = sameQuestionRepository.findAll().get(0)
        sameQuestion1.getFailedAnswers().size() == 2
        sameQuestion1.getFailedAnswers().contains(result2)
        sameQuestion1.getFailedAnswers().contains(result3)
        def sameQuestion2 = sameQuestionRepository.findAll().get(1)
        sameQuestion2.getFailedAnswers().size() == 2
        def sameQuestion3 = sameQuestionRepository.findAll().get(2)
        sameQuestion3.getFailedAnswers().size() == 2
    }

    def "create two failed answers with with different questions"() {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
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
        result.getSameQuestion().getFailedAnswers().size() == 0
        def result2 = failedAnswerRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getQuestionAnswer().getId() == questionAnswer2.getId()
        result2.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        result2.getAnswered()
        result2.getSameQuestion().getFailedAnswers().size() == 0
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(result)
        and:
        sameQuestionRepository.findAll().size() == 2
        def sameQuestion1 = sameQuestionRepository.findAll().get(0)
        sameQuestion1.getFailedAnswers().size() == 0
        def sameQuestion2 = sameQuestionRepository.findAll().get(0)
        sameQuestion2.getFailedAnswers().size() == 0
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
        sameQuestionRepository.count() == 1
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
        sameQuestionRepository.count() == 0
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
        sameQuestionRepository.count() == 0
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
        sameQuestionRepository.count() == 0

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
        sameQuestionRepository.count() == 0

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
        sameQuestionRepository.count() == 0

        where:
        questionAnswerId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}