package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACRONYM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_TYPE_NOT_DEFINED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DUPLICATE_COURSE_EXECUTION

@DataJpaTest
class CreateExternalCourseExecutionServiceSpockTest extends Specification {
    static final String COURSE_ONE = "CourseOne"
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"

    @Autowired
    AdministrationService administrationService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "the tecnico course exists and create execution course"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a courseDto"
        def courseDto = new CourseDto(course)
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        def result = administrationService.createExternalCourseExecution(courseDto)

        then: "the returned data are correct"
        result.courseType == Course.Type.TECNICO
        result.name == COURSE_ONE
        result.courseExecutionType == Course.Type.EXTERNAL
        result.acronym == ACRONYM_ONE
        result.academicTerm == ACADEMIC_TERM_ONE
        and: "course execution is created"
        course.getCourseExecutions().size() == 1
        def courseExecution = new ArrayList<>(course.getCourseExecutions()).get(0)
        courseExecution != null
        and: "has the correct value"
        courseExecution.type == Course.Type.EXTERNAL
        courseExecution.acronym == ACRONYM_ONE
        courseExecution.academicTerm == ACADEMIC_TERM_ONE
        courseExecution.getCourse() == course
    }

    def "the external course does not exist and create both, course and execution course"() {
        given: "a courseDto"
        def courseDto = new CourseDto()
        courseDto.setCourseType(Course.Type.EXTERNAL)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        def result = administrationService.createExternalCourseExecution(courseDto)

        then: "the returned data are correct"
        result.courseType == Course.Type.EXTERNAL
        result.name == COURSE_ONE
        result.courseExecutionType == Course.Type.EXTERNAL
        result.acronym == ACRONYM_ONE
        result.academicTerm == ACADEMIC_TERM_ONE
        and: "course is created"
        courseRepository.findAll().size() == 1
        def course = courseRepository.findByNameType(COURSE_ONE, Course.Type.EXTERNAL.name()).get()
        course != null
        course.type == Course.Type.EXTERNAL
        course.getName() == COURSE_ONE
        and: "course execution is created"
        courseExecutionRepository.findAll().size() == 1
        def courseExecution = courseExecutionRepository.findAll().get(0)
        courseExecution != null
        and: "has the correct value"
        courseExecution.type == Course.Type.EXTERNAL
        courseExecution.acronym ==ACRONYM_ONE
        courseExecution.academicTerm == ACADEMIC_TERM_ONE
        courseExecution.getCourse() == course
    }

    @Unroll
    def "invalid data in database where course is #isCourse, courseExecution is #isCourseExecution, type is #type, and errorMessage id #errorMessage"() {
        given: "a course"
        def course = createCourse(isCourse, COURSE_ONE, type)
        and: "a course execution"
        createCourseExecution(isCourseExecution, course, ACRONYM_ONE, ACADEMIC_TERM_ONE, type)
        and: "a courseDto"
        def courseDto = new CourseDto()
        courseDto.setCourseType(type)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        administrationService.createExternalCourseExecution(courseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        isCourse | isCourseExecution | type                 || errorMessage
        false    | false             | Course.Type.TECNICO  || COURSE_NOT_FOUND
        true     | true              | Course.Type.EXTERNAL || DUPLICATE_COURSE_EXECUTION
    }

    def createCourse(isCourse, courseName, type) {
        if (isCourse) {
            def course = new Course(courseName, type)
            courseRepository.save(course)
        }
    }

    def createCourseExecution(isCourseExecution, course, acronym, academicTerm, type) {
        if (isCourseExecution) {
            def courseExecution = new CourseExecution(course, acronym, academicTerm, type)
            courseExecutionRepository.save(courseExecution)
        }
    }

    @Unroll
    def "invalid arguments: type=#type | courseName=#courseName | acronym=#acronym | academicTerm=#academicTerm || errorMessage=#errorMessage "() {
        given: "a courseDto"
        def courseDto = new CourseDto()
        courseDto.setCourseType(type)
        courseDto.setName(courseName)
        courseDto.setAcronym(acronym)
        courseDto.setAcademicTerm(academicTerm)

        when:
        administrationService.createExternalCourseExecution(courseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        type                 | courseName | acronym     | academicTerm      || errorMessage
        null                 | COURSE_ONE | ACRONYM_ONE | ACADEMIC_TERM_ONE || COURSE_TYPE_NOT_DEFINED
        Course.Type.EXTERNAL | null       | ACRONYM_ONE | ACADEMIC_TERM_ONE || COURSE_NAME_IS_EMPTY
        Course.Type.EXTERNAL | "     "    | ACRONYM_ONE | ACADEMIC_TERM_ONE || COURSE_NAME_IS_EMPTY
        Course.Type.EXTERNAL | COURSE_ONE | null        | ACADEMIC_TERM_ONE || COURSE_EXECUTION_ACRONYM_IS_EMPTY
        Course.Type.EXTERNAL | COURSE_ONE | "      "    | ACADEMIC_TERM_ONE || COURSE_EXECUTION_ACRONYM_IS_EMPTY
        Course.Type.EXTERNAL | COURSE_ONE | ACRONYM_ONE | null              || COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
        Course.Type.EXTERNAL | COURSE_ONE | ACRONYM_ONE | "     "           || COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        AdministrationService administrationService() {
            return new AdministrationService()
        }

    }
}
