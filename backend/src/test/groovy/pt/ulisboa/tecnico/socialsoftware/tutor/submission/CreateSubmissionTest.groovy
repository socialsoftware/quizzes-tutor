package pt.ulisboa.tecnico.socialsoftware.tutor.submission.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.SubmissionService
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateSubmissionTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Topic"
    public static final String QUESTION_TITLE = "Question?"
    public static final String QUESTION_CONTENT = "Answer"
    public static final String STUDENT_NAME = "João Silva"
    public static final String STUDENT_USERNAME = "joaosilva"
    public static final String TEACHER_NAME = "Ana Rita"
    public static final String TEACHER_USERNAME = "anarita"

    @Autowired
    SubmissionService submissionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    SubmissionRepository submissionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

    def student
    def question
    def course
    def courseExecution
    def teacher

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        student = new User(STUDENT_NAME, STUDENT_USERNAME, User.Role.STUDENT)
        student.setEnrolledCoursesAcronyms(courseExecution.getAcronym())
        userRepository.save(student)
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, User.Role.TEACHER)
        userRepository.save(teacher)
        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setCourse(course)
        question.setStatus(Question.Status.SUBMITTED)
        questionRepository.save(question)
    }

    def "create submission with question not null and no topics"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setStudentId(student.getId())

        when: submissionService.createSubmission(question.getId(), submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion() == question
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == courseExecution
        !result.isAnonymous()
    }

    def "create an anonymous submission with question not null and no topics"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setStudentId(student.getId())
        submissionDto.setAnonymous(true)

        when: submissionService.createSubmission(question.getId(), submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion() == question
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getCourseExecution() == courseExecution
        result.isAnonymous()
    }

    def "create submission with question not null and a topic associated"() {
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setStudentId(student.getId())

        and: "a topic for question"
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        def topic = new Topic(course, topicDto)
        topicRepository.save(topic)
        def topics = new HashSet<Topic>();
        topics.add(topic)
        question.updateTopics(topics)
        questionRepository.save(question)

        when: submissionService.createSubmission(question.getId(), submissionDto)

        then: "the correct submission is in the repository"
        submissionRepository.count() == 1L
        def result = submissionRepository.findAll().get(0)
        result.getId() != null
        result.getUser() == student
        result.getQuestion() != null
        result.getQuestion() == question
        result.getQuestion().getStatus() == Question.Status.SUBMITTED
        result.getQuestion().getTopics().size() == 1
        result.getQuestion().getTopics().getAt(0) == topic
        result.getCourseExecution() == courseExecution
        !result.isAnonymous()
    }

    def "user is not a student"(){
        given: "a submissionDto for a teacher"
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setStudentId(teacher.getId())

        when: submissionService.createSubmission(question.getId(), submissionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
    }

    def "student that submits a question enrolled in course"(){
        given: "a submissionDto"
        def submissionDto = new SubmissionDto()
        submissionDto.setCourseExecutionId(courseExecution.getId())
        submissionDto.setStudentId(student.getId())

        when: submissionService.createSubmission(question.getId(), submissionDto)

        then:
        student.getEnrolledCoursesAcronyms().contains(courseExecution.getAcronym())
    }

    //add input verification tests with @Unroll


    @TestConfiguration
    static class SubmissionServiceImplTestContextConfiguration {
        @Bean
        SubmissionService submissionService() {
            return new SubmissionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
