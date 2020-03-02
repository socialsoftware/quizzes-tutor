package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification

@DataJpaTest
class CreateTecnicoCourseExecutionServiceSpockTest extends Specification {
    public static final String COURSE_ONE = "CourseOne"
    public static final String ACRONYM_ONE = "C12"
    public static final String ACADEMIC_TERM_ONE = "1º Semestre"

    @Autowired
    CourseService courseService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "the course exists and create execution course"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: 'a courseDto'
        def courseDto = new CourseDto(course)
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_ONE
        result.acronym == ACRONYM_ONE
        result.academicTerm == ACADEMIC_TERM_ONE
        and: 'is in the database'
        courseExecutionRepository.findAll().size() == 1
        def courseExecution = courseExecutionRepository.findAll().get(0)
        courseExecution != null
        and: 'has the correct value'
        courseExecution.acronym ==ACRONYM_ONE
        courseExecution.academicTerm == ACADEMIC_TERM_ONE
        courseExecution.getCourse() == course
    }

    def "the course does not exist and create both, course and execution course"() {
        given: 'a courseDto'
        def courseDto = new CourseDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_ONE
        result.acronym == ACRONYM_ONE
        result.academicTerm == ACADEMIC_TERM_ONE
        and: 'course is in the database'
        courseRepository.findAll().size() == 1
        def course = courseRepository.findByNameType(COURSE_ONE, Course.Type.TECNICO.name()).get()
        course != null
        course.getCourseExecutions().size() == 1
        and: 'course execution is in the database'
        courseExecutionRepository.findAll().size() == 1
        def courseExecution = courseExecutionRepository.findAll().get(0)
        courseExecution != null
        and: 'has the correct value'
        courseExecution.acronym ==ACRONYM_ONE
        courseExecution.academicTerm == ACADEMIC_TERM_ONE
        courseExecution.getCourse() == course
    }

    def "the course and course execution exist"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: 'a course execution'
        def courseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        and: 'a courseDto'
        def courseDto = new CourseDto(courseExecution)

        when:
        def result = courseService.createTecnicoCourseExecution(courseDto)

        then: "the returned data are correct"
        result.name == COURSE_ONE
        result.acronym == ACRONYM_ONE
        result.academicTerm == ACADEMIC_TERM_ONE
        and: 'are in the database'
        courseRepository.findAll().size() == 1
        courseExecutionRepository.findAll().size() == 1
    }

    def "course name is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName("  ")
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }

    def "execution course acronym is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym("   ")
        courseDto.setAcademicTerm(ACADEMIC_TERM_ONE)

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }

    def "execution course academic term is empty"() {
        given: 'a courseDto'
        def courseDto = new CourseDto()
        courseDto.setCourseType(Course.Type.TECNICO)
        courseDto.setName(COURSE_ONE)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm("   ")

        when:
        courseService.createTecnicoCourseExecution(courseDto)

        then:
        thrown(TutorException)
    }


    @TestConfiguration
    static class CourseServiceImplTestContextConfiguration {

        @Bean
        CourseService courseService() {
            return new CourseService()
        }
    }

}
