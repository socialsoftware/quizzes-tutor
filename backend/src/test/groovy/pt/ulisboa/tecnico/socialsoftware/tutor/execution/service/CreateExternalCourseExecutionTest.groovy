package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateExternalCourseExecutionTest extends SpockTest {

    def existingCourses
    def existingCourseExecutions

    def setup() {
        createExternalCourseAndExecution()

        existingCourses = courseRepository.findAll().size()
        existingCourseExecutions = courseExecutionRepository.findAll().size()
    }

    def "the tecnico course exists and create execution course"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        def courseDto = new CourseExecutionDto(externalCourse)
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setCourseExecutionType(Course.Type.EXTERNAL)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)

        when:
        def result = courseService.createExternalCourseExecution(courseDto)

        then: "the returned data are correct"
        result.courseType == Course.Type.TECNICO
        result.name == COURSE_1_NAME
        result.courseExecutionType == Course.Type.EXTERNAL
        result.acronym == COURSE_1_ACRONYM
        result.academicTerm == COURSE_1_ACADEMIC_TERM

        and: "course execution is created"
        externalCourse.getCourseExecutions().size() == 1
        def courseExecution = new ArrayList<>(externalCourse.getCourseExecutions()).get(0)
        courseExecution != null

        and: "has the correct value"
        courseExecution.type == Course.Type.EXTERNAL
        courseExecution.acronym == COURSE_1_ACRONYM
        courseExecution.academicTerm == COURSE_1_ACADEMIC_TERM
        courseExecution.getCourse() == externalCourse
    }

    def "the external course does not exist and create both, course and execution course"() {
        given: "a courseDto"
        def courseDto = new CourseExecutionDto()
        courseDto.setCourseType(Course.Type.EXTERNAL)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)

        when:
        def result = courseService.createExternalCourseExecution(courseDto)

        then: "the returned data are correct"
        result.courseType == Course.Type.EXTERNAL
        result.name == COURSE_1_NAME
        result.courseExecutionType == Course.Type.EXTERNAL
        result.acronym == COURSE_1_ACRONYM
        result.academicTerm == COURSE_1_ACADEMIC_TERM
        and: "course is created"
        courseRepository.findAll().size() == existingCourses + 1
        def course = courseRepository.findByNameType(COURSE_1_NAME, Course.Type.EXTERNAL.name()).get()
        course != null
        course.type == Course.Type.EXTERNAL
        course.getName() == COURSE_1_NAME
        and: "course execution is created"
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 1
        def courseExecution = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL.name()).get()
        courseExecution != null
        and: "has the correct value"
        courseExecution.type == Course.Type.EXTERNAL
        courseExecution.acronym == COURSE_1_ACRONYM
        courseExecution.academicTerm == COURSE_1_ACADEMIC_TERM
        courseExecution.getCourse() == course
    }

    @Unroll
    def "invalid arguments: type=#type | courseName=#courseName | acronym=#acronym | academicTerm=#academicTerm || errorMessage=#errorMessage "() {
        given: "a courseDto"
        def courseDto = new CourseExecutionDto()
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
        null                 | COURSE_1_NAME | COURSE_1_ACRONYM | COURSE_1_ACADEMIC_TERM || INVALID_TYPE_FOR_COURSE
        Course.Type.EXTERNAL | null       | COURSE_1_ACRONYM | COURSE_1_ACADEMIC_TERM || INVALID_NAME_FOR_COURSE
        Course.Type.EXTERNAL | "     "    | COURSE_1_ACRONYM | COURSE_1_ACADEMIC_TERM || INVALID_NAME_FOR_COURSE
        Course.Type.EXTERNAL | COURSE_1_NAME | null                       | COURSE_1_ACADEMIC_TERM || INVALID_ACRONYM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_1_NAME | "      "                   | COURSE_1_ACADEMIC_TERM || INVALID_ACRONYM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_1_NAME | COURSE_1_ACRONYM | null                   || INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION
        Course.Type.EXTERNAL | COURSE_1_NAME | COURSE_1_ACRONYM | "     "                || INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
