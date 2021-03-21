package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class DeleteExternalInactiveUsersTest extends SpockTest {

    def user1
    def user2

    def tecnicoCourse
    def tecnicoCourseExecution

    def userIdList

    def initialUserCount

    def setup() {
        createExternalCourseAndExecution()

        initialUserCount = userRepository.count()
        externalCourse = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(externalCourseExecution)

        tecnicoCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(tecnicoCourse)
        tecnicoCourseExecution = new CourseExecution(tecnicoCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(tecnicoCourseExecution)

        user1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user1)
        externalCourseExecution.addUser(user1)
        user1.addCourse(externalCourseExecution)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user2)
        externalCourseExecution.addUser(user2)
        user2.addCourse(externalCourseExecution)

        userIdList = new ArrayList<Integer>();
    }


    def "invalid course execution id" () {
        given: "an invalid execution id"
        def executionId = -1
        and: "a list of user id's"
        user1.getAuthUser().setActive(false)
        userIdList << user1.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "check if an execution is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        and: "no user was deleted"
        userRepository.count() == initialUserCount + 2
    }

    def "course execution not external" () {
        given: "a tecnico course execution id"
        def executionId = tecnicoCourseExecution.getId()
        and: "a list of user id's"
        user1.getAuthUser().setActive(false)
        user1.addCourse(tecnicoCourseExecution)
        tecnicoCourseExecution.addUser(user1)
        userIdList << user1.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList)

        then: "check if an exception is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL
        and: "no user was removed"
        tecnicoCourseExecution.getStudents().size() == 1
        userRepository.count() == initialUserCount + 2
    }

    def "tries to delete an active user" () {
        given: "and execution id"
        def executionId = externalCourseExecution.getId()
        and: "a list of user id's"
        user1.getAuthUser().setActive(true)
        userIdList << user1.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "an execption is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_IS_ACTIVE
        and: "the user is not removed from his course execution"
        externalCourseExecution.getStudents().size() == 2
        and: "no user is removed from the database"
        userRepository.count() == initialUserCount + 2
        userRepository.findById(user1.getId()) != null
        userRepository.findById(user2.getId()) != null
    }


    def "there are some inactive external users and deletes them" (){
        given: "an execution id"
        def executionId = externalCourseExecution.getId()
        and: "a list of user id's"
        user1.getAuthUser().setActive(false)
        user2.getAuthUser().setActive(false)
        userIdList << user1.getId()
        userIdList << user2.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "check that that no user was removed from the course execution"
        externalCourseExecution.getStudents().size() == 0
        and: "there are no external users in the database"
        userRepository.count() == initialUserCount // The 3 Demo-Users
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
