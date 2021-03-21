package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class GetCourseExecutionsTest extends SpockTest {
    def existingCourses
    def existingCourseExecutions

    def setup() {
        createExternalCourseAndExecution()

        existingCourses = courseRepository.findAll().size()
        existingCourseExecutions = courseExecutionRepository.findAll().size()
    }

    def "returned a tecnico course with 0 info"() {
        when:
        def result = courseService.getCourseExecutions(User.Role.ADMIN)

        then: "the returned data are correct"
        result.size() == existingCourses
        def tecnicoCourse = result.get(0)
        tecnicoCourse.name == COURSE_1_NAME
        tecnicoCourse.courseType == Course.Type.TECNICO
        tecnicoCourse.acronym == COURSE_1_ACRONYM
        tecnicoCourse.academicTerm == COURSE_1_ACADEMIC_TERM
        tecnicoCourse.courseExecutionType == Course.Type.TECNICO
        tecnicoCourse.numberOfQuestions == 0
        tecnicoCourse.numberOfQuizzes == 0
        tecnicoCourse.numberOfActiveTeachers == 0
        tecnicoCourse.numberOfInactiveTeachers == 0
        tecnicoCourse.numberOfActiveStudents == 0
        tecnicoCourse.numberOfInactiveStudents == 0
    }

    def "returned an external course with more info"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        def courseExecution = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        def teacher = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.addCourse(courseExecution)

        def student = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)

        Question question = new Question()
        question.setTitle("Title")
        question.setCourse(externalCourse)

        Quiz quiz = new Quiz()
        quiz.setTitle("Title")
        quiz.setCourseExecution(courseExecution)

        when:
        def result = courseService.getCourseExecutions(User.Role.ADMIN)

        then: "the returned data are correct"
        result.size() == 1
        def externalCourse = result.get(0)
        externalCourse.name == COURSE_1_NAME
        externalCourse.courseType == Course.Type.TECNICO
        externalCourse.acronym == COURSE_2_ACRONYM
        externalCourse.academicTerm == COURSE_2_ACADEMIC_TERM
        externalCourse.courseExecutionType == Course.Type.EXTERNAL
        externalCourse.numberOfQuestions == 1
        externalCourse.numberOfQuizzes == 1
        externalCourse.numberOfActiveTeachers == 1
        externalCourse.numberOfInactiveTeachers == 0
        externalCourse.numberOfActiveStudents == 1
        externalCourse.numberOfInactiveStudents == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
