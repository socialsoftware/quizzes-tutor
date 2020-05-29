package pt.ulisboa.tecnico.socialsoftware.tutor.course.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetCourseExecutionsTest extends SpockTest {
    static final String COURSE_ONE = "CourseOne"
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"
    static final String ACRONYM_TWO = "C22"
    static final String ACADEMIC_TERM_TWO = "2º Semestre"

    def existingCourses
    def existingCourseExecutions

    def setup() {
        existingCourses = courseRepository.findAll().size()
        existingCourseExecutions = courseExecutionRepository.findAll().size()
    }

    def "returned a tecnico course with 0 info"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)

        and: "a tecnico course execution"
        def courseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        when:
        def result = courseService.getCourseExecutions(User.Role.ADMIN)

        then: "the returned data are correct"
        result.size() == existingCourses + 1
        def tecnicoCourse = result.get(0)
        tecnicoCourse.name == COURSE_ONE
        tecnicoCourse.courseType == Course.Type.TECNICO
        tecnicoCourse.acronym == ACRONYM_ONE
        tecnicoCourse.academicTerm == ACADEMIC_TERM_ONE
        tecnicoCourse.courseExecutionType == Course.Type.TECNICO
        tecnicoCourse.numberOfQuestions == 0
        tecnicoCourse.numberOfQuizzes == 0
        tecnicoCourse.numberOfTeachers == 0
        tecnicoCourse.numberOfStudents == 0
    }

    def "returned an external course with more info"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)

        and: "an external course execution"
        def courseExecution = new CourseExecution(course, ACRONYM_TWO, ACADEMIC_TERM_TWO, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        and: "a teacher"
        def teacher = new User("NAME 1", "USERNAME 1", User.Role.TEACHER)
        courseExecution.addUser(teacher)

        and: "a student"
        def student = new User("NAME 2", "USERNAME 2", User.Role.STUDENT)
        courseExecution.addUser(student)

        and: "a question"
        def question = new Question()
        question.setTitle("Title")
        course.addQuestion(question)

        and: "a quiz"
        def quiz = new Quiz()
        quiz.setTitle("Title")
        courseExecution.addQuiz(quiz)

        when:
        def result = courseService.getCourseExecutions(User.Role.ADMIN)

        then: "the returned data are correct"
        result.size() == existingCourses + 1
        def externalCourse = result.get(0)
        externalCourse.name == COURSE_ONE
        externalCourse.courseType == Course.Type.TECNICO
        externalCourse.acronym == ACRONYM_TWO
        externalCourse.academicTerm == ACADEMIC_TERM_TWO
        externalCourse.courseExecutionType == Course.Type.EXTERNAL
        externalCourse.numberOfQuestions == 1
        externalCourse.numberOfQuizzes == 1
        externalCourse.numberOfTeachers == 1
        externalCourse.numberOfStudents == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
