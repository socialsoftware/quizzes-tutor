package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
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
    }

    def "reset all demo students"() {
        given:
        def user1 = userService.createDemoStudent()
        def user1Username = user1.username
        def user2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.DEMO)
        userRepository.save(user2)

        when:
        userService.resetDemoStudents()

        then:
        AuthUser authUser1 = authUserRepository.findAuthUserByUsername(user1Username).orElse(null)
        AuthUser authUser2 = authUserRepository.findAuthUserByUsername(USER_2_USERNAME).orElse(null)
        authUser1 == null
        authUser2.id == user2.authUser.id
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
