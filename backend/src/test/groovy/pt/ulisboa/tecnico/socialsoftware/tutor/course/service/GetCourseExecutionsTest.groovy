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
    def existingCourses
    def existingCourseExecutions

    def setup() {
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

        course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        def teacher = new User(USER_1_NAME, USER_1_USERNAME, User.Role.TEACHER)
        teacher.setState(User.State.ACTIVE)
        teacher.addCourse(courseExecution)

        def student = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        student.setState(User.State.ACTIVE)
        student.addCourse(courseExecution)
        System.out.println("\n\n\n\n"+student.getState()+"\n\n\n")

        Question question = new Question()
        question.setTitle("Title")
        question.setCourse(course)

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
