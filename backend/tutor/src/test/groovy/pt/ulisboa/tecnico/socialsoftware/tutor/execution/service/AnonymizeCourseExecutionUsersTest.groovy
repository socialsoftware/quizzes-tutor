package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

import java.util.stream.Collectors

@DataJpaTest
class AnonymizeCourseExecutionUsersTest extends SpockTest {
    def user

    def tecnicoCourse
    def tecnicoCourseExecution

    def answerItemsIds

    def setup() {
        createExternalCourseAndExecution()

        tecnicoCourse = new Course(COURSE_1_NAME, CourseType.TECNICO)
        courseRepository.save(tecnicoCourse)
        tecnicoCourseExecution = new CourseExecution(tecnicoCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, CourseType.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(tecnicoCourseExecution)

        user = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT, false)
        userRepository.save(user)
        tecnicoCourseExecution.addUser(user)
        user.addCourse(tecnicoCourseExecution)

        answerItemsIds = questionAnswerItemRepository.findQuestionAnswerItemsByUsername(USER_1_USERNAME)
                .stream().map({ answerItem -> answerItem.getId() })
                .collect(Collectors.toList())
    }

    def "anonymize course execution users"() {
        when:
        courseService.anonymizeCourseExecutionUsers(tecnicoCourseExecution.id)

        then:
        user.name == "Student " + user.id
        user.getUsername() == "student-" + user.id
        answerItemsIds == questionAnswerItemRepository.findQuestionAnswerItemsByUsername(user.getUsername())
                .stream().map({ answerItem -> answerItem.getId() })
                .collect(Collectors.toList())
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}