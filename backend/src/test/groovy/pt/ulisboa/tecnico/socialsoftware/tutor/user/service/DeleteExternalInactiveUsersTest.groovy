package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class DeleteExternalInactiveUsersTest extends SpockTest{

    def user1
    def user2

    List userIdList

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        userIdList = new ArrayList<Integer>();
    }



    def "tries to delete an active user" () {
        given: "one active users"
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user1.setState(User.State.INACTIVE)
        userRepository.save(user1)
        courseExecution.addUser(user1)

        and: "a userId list"
        userIdList << user1.getId()

        when:
        userService.deleteExternalInactiveUsers(userIdList)

        then: "the user is not removed from his course execution"
        courseExecution.getStudents().size() == 1
        courseExecution.getStudents().toList().get(0).getId() == user1.getId()

        and: "the user is not removed from the database"
        userRepository.count() == 4
        userRepository.findById(user1.getId()) != null
    }


    def "there are some inactive external users and deletes them" (){
        given: "two inactive users"
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user1.setState(User.State.INACTIVE)
        userRepository.save(user1)
        courseExecution.addUser(user1)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user2.setState(User.State.INACTIVE)
        userRepository.save(user2)
        courseExecution.addUser(user2)

        and: "a userId list"

        userIdList << user1.getId()
        userIdList << user2.getId()

        when:
        userService.deleteExternalInactiveUsers(userIdList);

        then: "check that that no user was removed from the course execution"
        courseExecution.getStudents().size() == 0

        and: "there are no external users in the database"
        userRepository.count() == 3 // The 3 Demo-Users
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
