package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser

@DataJpaTest
class ResetDemoStudentTest extends SpockTest {

    def course
    def courseExecution
    def quiz
    def quizQuestion
    def option

    def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

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

        option = new Option()
        option.setContent("Content")
        option.setSequence(1)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        questionRepository.save(question)

        QuestionDto questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quizDto.setQuestions(questions)

        quiz = new Quiz(quizDto)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution);
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question, 1)
        quizQuestionRepository.save(quizQuestion)
    }

    def "reset all demo students"() {
        given:
        def user1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.DEMO)
        userRepository.save(user1)
        user1.numberOfStudentAnswers = null

        def user2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user2)
        user2.numberOfStudentAnswers = null

        and: "users answers"
        QuizAnswer quizAnswer1 = new QuizAnswer(user1, quiz)
        quizAnswer1.setCompleted(true)
        new QuestionAnswer(quizAnswer1, quizQuestion, 1, option, 1)
        quizAnswerRepository.save(quizAnswer1)
        def user1Answers = user1.getNumberOfStudentAnswers()

        QuizAnswer quizAnswer2 = new QuizAnswer(user2, quiz)
        quizAnswer2.setCompleted(true)
        new QuestionAnswer(quizAnswer2, quizQuestion, 1, option, 1)
        quizAnswerRepository.save(quizAnswer2)
        def user2Answers = user2.getNumberOfStudentAnswers()

        when:
        userService.resetDemoStudents()

        then:
        user1.getNumberOfStudentAnswers() != user1Answers
        user1.getNumberOfStudentAnswers() == 0
        user2.getNumberOfStudentAnswers() == user2Answers
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
