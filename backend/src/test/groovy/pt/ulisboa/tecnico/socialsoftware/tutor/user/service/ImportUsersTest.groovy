package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.Notification
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

@DataJpaTest
class ImportUsersTest extends SpockTest {

    Course course
    CourseExecution courseExecution

    def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)
    }

    def 'Import users from csv file' () {
        given: "number of users in dataBase"
        def usersInDataBase = userRepository.count()
        and: "inputStream"
        InputStream csvFile = new FileInputStream(CSVFILE);
        when:
        userService.importListOfUsers(csvFile, courseExecution.getId())
        then:
        userRepository.findAll().size() == usersInDataBase + NUMBER_OF_USERS_IN_FILE;
    }

    def 'Csv file has wrong format on some lines, no users are added' () {
        given: "number of users in dataBase"
        def usersInDataBase = userRepository.count()
        and: "wrong formatted InputStream"
        InputStream csvBadFormatFile = new FileInputStream(CSVBADFORMATFILE);
        when:
        def result  = userService.importListOfUsers(csvBadFormatFile, courseExecution.getId())
        then:
        result.response.name == COURSE_1_NAME
        and:
        result.notification.getExceptions().size() > 0
        for(TutorException e : result.notification.getExceptions())
            e.getErrorMessage() == ErrorMessage.WRONG_FORMAT_ON_CSV_LINE
        and:
        userRepository.findAll().size() == usersInDataBase
    }

    def 'The course execution does not exist' () {
        given: "number of users in dataBase"
        def usersInDataBase = userRepository.count()
        and: "a invalid course execution id"
        def executionId = -1
        and: "inputStream"
        InputStream csvFile = new FileInputStream(CSVFILE);
        when:
        userService.importListOfUsers(csvFile, executionId)
        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        and:
        userRepository.findAll().size() == usersInDataBase
    }

    def 'Csv file has wrong format for roles' () {
        given: "number of users in dataBase"
        def usersInDataBase = userRepository.count()
        and: "wrong role formatted InputStream"
        InputStream csvImportUsersBadRoleFormat = new FileInputStream(CSVIMPORTUSERSBADROLEFORMAT);
        when:
        def result = userService.importListOfUsers(csvImportUsersBadRoleFormat, courseExecution.getId())
        then:
        result.response.name == COURSE_1_NAME
        and:
        result.notification.getExceptions().size() > 0
        for(TutorException e : result.notification.getExceptions())
            e.getErrorMessage() == ErrorMessage.WRONG_FORMAT_ON_CSV_LINE
        and:
        userRepository.findAll().size() == usersInDataBase
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}