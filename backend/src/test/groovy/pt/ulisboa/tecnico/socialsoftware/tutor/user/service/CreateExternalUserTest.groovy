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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto
import spock.lang.Unroll

@DataJpaTest
class CreateExternalUserTest extends SpockTest {

    static final String EMAIL = "pedro.pereira2909@gmail.com"

    ExternalUserDto externalUserDto


    def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)
    }


    def "the course execution does not exist" (){
        given: "a invalid course execution id"
        def executionId = -1
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        userService.createExternalUser(executionId, externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        userRepository.count() == 3

    }

    def "the course execution exists, but it is not external" (){
        given: "a external course execution"
        courseExecution.setType(Course.Type.TECNICO)
        def executionId = courseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        userService.createExternalUser(executionId, externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL
        userRepository.count() == 3

    }

    def "the course execution exists, the username does not exist, create the user and associate the user with the course execution" (){
        given: "a external course execution"
        def executionId = courseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        def result = userService.createExternalUser(executionId, externalUserDto)

        then:"the user is saved in the database"
        userRepository.findAll().size() == 4
        and: "checks if user data is correct"
        result.getUsername() == EMAIL
        result.getEmail() == EMAIL
        and:"checks if the user and the course execution are associated"
        result.getCourseExecutions().size() == 1
        result.getCourseExecutions().get(0).getAcronym() == COURSE_1_ACRONYM
        result.getCourseExecutions().get(0).getAcademicTerm() == COURSE_1_ACADEMIC_TERM
        courseExecution.getUsers().size() == 1
        courseExecution.getUsers().toList().get(0).getId() == result.getId()

    }

    def "the course execution exists, the username exists and associate the user with the course execution" (){
        given: "a user"
        def user2 = new User("", EMAIL, User.Role.STUDENT)
        user2.addCourse(courseExecution)
        courseExecution.addUser(user2)
        userRepository.save(user2)
        and: "a external course execution"
        def executionId = courseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        def result = userService.createExternalUser(executionId, externalUserDto)

        then:"the user is saved in the database"
        System.out.println(userRepository.findAll())
        userRepository.count() == 4
        and: "checks if user data is correct"
        result.getUsername() == EMAIL
        result.getEmail() == EMAIL
        and:"checks if the user and the course execution are associated"
        result.getCourseExecutions().size() == 1
        result.getCourseExecutions().get(0).getAcronym() == COURSE_1_ACRONYM
        result.getCourseExecutions().get(0).getAcademicTerm() == COURSE_1_ACADEMIC_TERM
        courseExecution.getUsers().size() == 1
        courseExecution.getUsers().toList().get(0).getId() == result.getId()

    }

    @Unroll
    def "invalid arguments: email=#email | password=#password | role=#role"(){
        given: "a invalid course execution id"
        def executionId = courseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(email)

        when:
        userService.createExternalUser(executionId, externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage


        where:
        email       | role                     || errorMessage
        null        | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        ""          | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        EMAIL       | null                     || ErrorMessage.INVALID_ROLE

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}