package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

@DataJpaTest
class CreateTecnicoCourseExecutionTest extends SpockTest {

    def existingCourses
    def existingCourseExecutions

    def setup() {
        createExternalCourseAndExecution()

        existingCourses = courseRepository.findAll().size()
        existingCourseExecutions = courseExecutionRepository.findAll().size()
    }

    def "the course exists and create execution course"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()

        def courseDto = new CourseExecutionDto(externalCourse)
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_1_NAME
        result.acronym == COURSE_1_ACRONYM
        result.academicTerm == COURSE_1_ACADEMIC_TERM
        and: 'is in the database'
        courseExecutionRepository.findAll().size() == 1
        def courseExecution = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO.name()).get()
        courseExecution != null
        and: 'has the correct value'
        courseExecution.acronym == COURSE_1_ACRONYM
        courseExecution.academicTerm == COURSE_1_ACADEMIC_TERM
        courseExecution.getCourse() == externalCourse
        courseExecution.endDate == LOCAL_DATE_TODAY
    }

    def "the course does not exist and create both, course and execution course"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        def courseDto = new CourseExecutionDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_1_NAME
        result.acronym == COURSE_1_ACRONYM
        result.academicTerm == COURSE_1_ACADEMIC_TERM
        and: 'course is in the database'
        courseRepository.findAll().size() == 1
        def course = courseRepository.findByNameType(COURSE_1_NAME, Course.Type.TECNICO.name()).get()
        course != null
        course.getCourseExecutions().size() == 1
        and: 'course execution is in the database'
        courseExecutionRepository.findAll().size() == 1
        def courseExecution = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO.name()).get()
        courseExecution != null
        and: 'has the correct value'
        courseExecution.acronym == COURSE_1_ACRONYM
        courseExecution.academicTerm == COURSE_1_ACADEMIC_TERM
        courseExecution.getCourse() == course
        courseExecution.endDate == LOCAL_DATE_TODAY
    }

    def "the course and course execution exist"() {
        def courseDto = new CourseExecutionDto(externalCourseExecution)

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_1_NAME
        result.acronym == COURSE_1_ACRONYM
        result.academicTerm == COURSE_1_ACADEMIC_TERM
        and: 'are in the database'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "course name is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseExecutionDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName("  ")
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }

    def "execution course acronym is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseExecutionDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym("   ")
        courseDto.setAcademicTerm(COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }

    def "execution course academic term is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseExecutionDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_1_NAME)
        courseDto.setAcronym(COURSE_1_ACRONYM)
        courseDto.setAcademicTerm("   ")

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
