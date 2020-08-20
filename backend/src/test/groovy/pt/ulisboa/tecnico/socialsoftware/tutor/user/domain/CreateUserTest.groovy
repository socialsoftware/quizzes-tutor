package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll;

@DataJpaTest
class CreateUserTest extends SpockTest {

    def user
    def quiz

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, false)
        userRepository.save(user)
        user.setKey(user.getId())

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
        questionRepository.save(question)

        Option option = new Option()
        option.setSequence(1)
        option.setCorrect(true)
        question.addOption(option)

        QuestionDto questionDto = new QuestionDto(question)
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

        QuestionAnswer questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1, option, 1)
    }

    def "create User: name, username, role" (){
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, true, false)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.STUDENT
    }

    def "create User: name, username, email, role, state, admin" (){
        when:
        def result = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, false)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getEmail() == USER_1_EMAIL
        result.getRole() == User.Role.STUDENT
        !result.isActive()
        !result.isAdmin()
    }

    def "checkConfirmationToken: correct token and date has not expired" (){
        given:
        user.setConfirmationToken(USER_1_TOKEN)
        user.setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        user.checkConfirmationToken(USER_1_TOKEN)

        then:
        noExceptionThrown()
    }

    def "checkConfirmationToken: correct token but date has expired" (){
        given:
        user.setConfirmationToken(USER_1_TOKEN)
        user.setTokenGenerationDate(LOCAL_DATE_BEFORE)

        when:
        user.checkConfirmationToken(USER_1_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.EXPIRED_CONFIRMATION_TOKEN
    }

    def "checkConfirmationToken: incorrect token" (){
        given:
        user.setConfirmationToken(USER_1_TOKEN)
        user.setTokenGenerationDate(LOCAL_DATE_TODAY)

        when:
        user.checkConfirmationToken(USER_2_TOKEN)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }

    @Unroll
    def "get number of quizzes" () {
        given:
        user.numberOfTeacherQuizzes = null
        user.numberOfStudentQuizzes = null
        user.numberOfInClassQuizzes = null
        quiz.setType(type)

        when:
        def nTeacherQuizzes = user.getNumberOfTeacherQuizzes()
        def nStudentQuizzes = user.getNumberOfStudentQuizzes()
        def nInClassQuizzes = user.getNumberOfInClassQuizzes()

        then:
        nTeacherQuizzes == nQuizzes1
        nStudentQuizzes == nQuizzes2
        nInClassQuizzes == nQuizzes3

        where:
        type        || nQuizzes1 | nQuizzes2 | nQuizzes3
        "PROPOSED"  || 1         | 0         | 0
        "GENERATED" || 0         | 1         | 0
        "IN_CLASS"  || 0         | 0         | 1
        "TEST"      || 0         | 0         | 0
        "EXAM"      || 0         | 0         | 0
    }

    @Unroll
    def "get number of answers" () {
        given:
        user.numberOfTeacherAnswers = null
        user.numberOfStudentAnswers = null
        user.numberOfInClassAnswers = null
        quiz.setType(type)

        when:
        def nTeacherAnswers = user.getNumberOfTeacherAnswers()
        def nStudentAnswers = user.getNumberOfStudentAnswers()
        def nInClassAnswers = user.getNumberOfInClassAnswers()

        then:
        nTeacherAnswers == nAnswers1
        nStudentAnswers == nAnswers2
        nInClassAnswers == nAnswers3

        where:
        type        || nAnswers1 | nAnswers2 | nAnswers3
        "PROPOSED"  || 1         | 0         | 0
        "GENERATED" || 0         | 1         | 0
        "IN_CLASS"  || 0         | 0         | 1
        "TEST"      || 0         | 0         | 0
        "EXAM"      || 0         | 0         | 0
    }

    @Unroll
    def "get number of correct answers" () {
        given:
        user.numberOfCorrectTeacherAnswers = null
        user.numberOfCorrectStudentAnswers = null
        user.numberOfCorrectInClassAnswers = null
        quiz.setType(type)

        when:
        def nTeacherAnswers = user.getNumberOfCorrectTeacherAnswers()
        def nStudentAnswers = user.getNumberOfCorrectStudentAnswers()
        def nInClassAnswers = user.getNumberOfCorrectInClassAnswers()

        then:
        nTeacherAnswers == nAnswers1
        nStudentAnswers == nAnswers2
        nInClassAnswers == nAnswers3

        where:
        type        || nAnswers1 | nAnswers2 | nAnswers3
        "PROPOSED"  || 1         | 0         | 0
        "GENERATED" || 0         | 1         | 0
        "IN_CLASS"  || 0         | 0         | 1
        "TEST"      || 0         | 0         | 0
        "EXAM"      || 0         | 0         | 0
    }


    def "addCourse" (){
        given:
        def course = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def previousNumberOfUsers = courseExecution.getUsers().size()
        def previousNumberOfCourses = user.getCourseExecutions().size()

        when:
        user.addCourse(courseExecution)

        then:
        courseExecution.getUsers().size() == previousNumberOfUsers + 1
        user.getCourseExecutions().size() == previousNumberOfCourses + 1
    }

    def "addQuizAnswer" (){
        given:
        def quizAnswer = new QuizAnswer()
        def previousNumberQuizAnswers = user.getQuizAnswers().size()

        when:
        user.addQuizAnswer(quizAnswer)

        then:
        user.getQuizAnswers().size() == previousNumberQuizAnswers + 1
    }

    def "remove from course executions" (){
        given:
        def course = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        user.addCourse(courseExecution)

        def previousNumberOfUsers = courseExecution.getUsers().size()

        when:
        user.remove()

        then:
        courseExecution.getUsers().size() == previousNumberOfUsers - 1
    }

    @Unroll
    def "get user authorities" (){
        given:
        user.setAdmin(admin)
        user.setRole(role)

        when:
        def result  = user.getAuthorities()

        then:
        result.size() == size
        def iter = result.iterator()
        iter.next().getAuthority() == firstAuthority
        if (result.size() > 1)
            iter.next().getAuthority() == secondAuthority

        where:
        admin  | role                   || size | firstAuthority  | secondAuthority
        false  | User.Role.STUDENT      || 1    | ROLE_STUDENT    | null
        false  | User.Role.TEACHER      || 1    | ROLE_TEACHER    | null
        false  | User.Role.ADMIN        || 1    | ROLE_ADMIN      | null
        false  | User.Role.DEMO_ADMIN   || 1    | ROLE_DEMO_ADMIN | null
        true   | User.Role.STUDENT      || 2    | ROLE_STUDENT    | ROLE_ADMIN
        true   | User.Role.TEACHER      || 2    | ROLE_TEACHER    | ROLE_ADMIN
        true   | User.Role.ADMIN        || 2    | ROLE_ADMIN      | ROLE_ADMIN
        true   | User.Role.DEMO_ADMIN   || 2    | ROLE_DEMO_ADMIN | ROLE_ADMIN
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
        Quiz.QuizType.PROPOSED  || 1                | 0               | 0
        Quiz.QuizType.GENERATED || 0                | 1               | 0
        Quiz.QuizType.IN_CLASS  || 0                | 0               | 1
        Quiz.QuizType.TEST      || 0                | 0               | 0
        Quiz.QuizType.EXAM      || 0                | 0               | 0
    }

    @Unroll
    def "increase number answers" () {
        when:
        user.increaseNumberOfCorrectAnswers(type)

        then:
        user.getNumberOfCorrectTeacherAnswers() == nTeacherAnswers
        user.getNumberOfCorrectStudentAnswers() == nStudentAnswers
        user.getNumberOfCorrectInClassAnswers() == nInClassAnswers

        where:
        type                    || nTeacherAnswers  | nStudentAnswers | nInClassAnswers
        Quiz.QuizType.PROPOSED  || 1                | 0               | 0
        Quiz.QuizType.GENERATED || 0                | 1               | 0
        Quiz.QuizType.IN_CLASS  || 0                | 0               | 1
        Quiz.QuizType.TEST      || 0                | 0               | 0
        Quiz.QuizType.EXAM      || 0                | 0               | 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
