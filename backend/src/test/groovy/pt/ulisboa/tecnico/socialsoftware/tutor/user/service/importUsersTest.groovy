package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
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
        userRepository.count() == usersInDataBase + noOfUsersInFile;
    }

    def 'Csv file has wrong format' () {
        given: "wrong formatted InputStream"
        InputStream csvBadFormatFile = new FileInputStream(CSVBADFORMATFILE);
        when:
        userService.importListOfUsers(csvBadFormatFile, courseExecution.getId())
        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_CSV_FILE_FORMAT
    }

    def 'The course execution does not exist' () {
        given: "a invalid course execution id"
        def executionId = -1
        and: "inputStream"
        InputStream csvFile = new FileInputStream(CSVFILE);
        when:
        userService.importListOfUsers(csvFile, executionId)
        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
