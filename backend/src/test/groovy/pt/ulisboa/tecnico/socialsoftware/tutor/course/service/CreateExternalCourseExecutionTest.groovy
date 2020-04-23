package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateExternalCourseExecutionTest extends Specification {
    static final String COURSE_ONE = "CourseOne"
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"

    @Autowired
    CourseService courseService

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
        courseDto.setCourseExecutionType(Course.Type.EXTERNAL)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)
        def a = courseRepository.findAll().size()

        when:
        def result = courseService.createExternalCourseExecution(courseDto)

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
        def result = courseService.createExternalCourseExecution(courseDto)

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
    def "invalid arguments: type=#type | courseName=#courseName | acronym=#acronym | academicTerm=#academicTerm || errorMessage=#errorMessage "() {
        given: "a courseDto"
        def courseDto = new CourseDto()
        courseDto.setCourseType(type)
        courseDto.setName(courseName)
        courseDto.setAcronym(acronym)
        courseDto.setAcademicTerm(academicTerm)

        when:
        courseService.createExternalCourseExecution(courseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        type                 | courseName | acronym     | academicTerm      || errorMessage
        null                 | COURSE_ONE | ACRONYM_ONE | ACADEMIC_TERM_ONE || INVALID_TYPE_FOR_COURSE
        Course.Type.EXTERNAL | null       | ACRONYM_ONE | ACADEMIC_TERM_ONE || INVALID_NAME_FOR_COURSE
        Course.Type.EXTERNAL | "     "    | ACRONYM_ONE | ACADEMIC_TERM_ONE || INVALID_NAME_FOR_COURSE
        Course.Type.EXTERNAL | COURSE_ONE | null        | ACADEMIC_TERM_ONE || INVALID_ACRONYM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_ONE | "      "    | ACADEMIC_TERM_ONE || INVALID_ACRONYM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_ONE | ACRONYM_ONE | null              || INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_ONE | ACRONYM_ONE | "     "           || INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        CourseService courseService() {
            return new CourseService()
        }
    }
}
