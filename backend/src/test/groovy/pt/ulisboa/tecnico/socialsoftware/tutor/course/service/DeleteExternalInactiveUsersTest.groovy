package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class DeleteExternalInactiveUsersTest extends SpockTest {

    def user1
    def user2

    Course tecnicoCourse
    CourseExecution tecnicoCourseExecution

    List userIdList

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        tecnicoCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(tecnicoCourse)
        tecnicoCourseExecution = new CourseExecution(tecnicoCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(tecnicoCourseExecution)

        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        userRepository.save(user1)
        courseExecution.addUser(user1)
        user1.addCourse(courseExecution)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        userRepository.save(user2)
        courseExecution.addUser(user2)
        user2.addCourse(courseExecution)

        userIdList = new ArrayList<Integer>();
    }


    def "invalid course execution id" () {
        given: "an invalid execution id"
        def executionId = -1

        and: "a list of user id's"
        user1.setState(User.State.INACTIVE)
        userIdList << user1.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "check if an execution is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
    }

    def "course execution not external" () {
        given: "a tecnico course execution id"
        def executionId = tecnicoCourseExecution.getId()

        and: "a list of user id's"
        user1.setState(User.State.INACTIVE)
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
        userRepository.count() == 5
    }

    def "tries to delete an active user" () {
        given: "and execution id"
        def executionId = courseExecution.getId()

        and: "a list of user id's"
        user1.setState(User.State.ACTIVE)
        userIdList << user1.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "an execption is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_IS_ACTIVE

        and: "the user is not removed from his course execution"
        courseExecution.getStudents().size() == 2 // No student was removed


        and: "no user is removed from the database"
        userRepository.count() == 5
        userRepository.findById(user1.getId()) != null
        userRepository.findById(user2.getId()) != null

    }


    def "there are some inactive external users and deletes them" (){
        given: "an execution id"
        def executionId = courseExecution.getId()

        and: "a list of user id's"
        user1.setState(User.State.INACTIVE)
        user2.setState(User.State.INACTIVE)
        userIdList << user1.getId()
        userIdList << user2.getId()

        when:
        courseService.deleteExternalInactiveUsers(executionId, userIdList);

        then: "check that that no user was removed from the course execution"
        courseExecution.getStudents().size() == 0

        and: "there are no external users in the database"
        userRepository.count() == 3 // The 3 Demo-Users


    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
