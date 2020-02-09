package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Specification

@DataJpaTest
class GetCoursesServiceSpockTest extends Specification {
    public static final String COURSE_ONE = "CourseOne"
    public static final String COURSE_TWO = "CourseTwo"

    @Autowired
    CourseService courseService

    @Autowired
    CourseRepository courseRepository

    def "two courses"() {
        given: "two courses"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        course = new Course(COURSE_TWO, Course.Type.TECNICO)
        courseRepository.save(course)

        when:
        def result = courseService.getCourses()

        then: "the returned data are correct"
        result.size() == 2
        and: "correctly sorted"
        result.get(0).name == COURSE_ONE
        result.get(1).name == COURSE_TWO
    }

    def "no courses"() {
        when:
        def result = courseService.getCourses()

        then: "the returned data are correct"
        result.size() == 0
    }

    @TestConfiguration
    static class CourseServiceImplTestContextConfiguration {

        @Bean
        CourseService courseService() {
            return new CourseService()
        }
    }

}
