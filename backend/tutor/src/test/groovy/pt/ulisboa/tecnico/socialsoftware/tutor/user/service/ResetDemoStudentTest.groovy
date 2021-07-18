package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

import java.time.LocalDateTime

@DataJpaTest
class ResetDemoStudentTest extends SpockTest {

    def course
    def courseExecution

    def setup(){
        course = new Course(COURSE_1_NAME, CourseType.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "reset all demo students"() {
        given:
        def birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        def user1 = new User("Demo-Student-" + birthDate, "Demo-Student-" + birthDate
                , Role.STUDENT)
        userRepository.save(user1)
        def user2 = new User(USER_2_NAME, USER_2_USERNAME,
                Role.STUDENT)
        userRepository.save(user2)

        when:
        userService.resetDemoStudents()

        then:
        userRepository.findAll().size() == 1
        userRepository.findAll().get(0).getUsername().equals(USER_2_USERNAME)
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
