package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolveQuizTournamentIT extends SpockTest {

    @LocalServerPort
    private int port

    def assessmentDto

    def response
    def user

    def course
    def courseExecution

    def question1

    def topicDto1
    def tournamentDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        def loggedUser = restClient.get(
                path: '/auth/external',
                query: [
                        email: USER_1_EMAIL,
                        password: USER_1_PASSWORD,
                ],
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization']  = "Bearer " + loggedUser.data.token

        topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        def topic1 = new Topic(course, topicDto1);
        topicRepository.save(topic1)
        topicDto1 = new TopicDto(topic1)

        def topics = new HashSet<Integer>()
        topics.add(topicDto1.getId())

        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(topicDto1)

        def topicConjunctionList = new ArrayList<TopicConjunctionDto>()
        topicConjunctionList.add(topicConjunctionDto)

        assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_1_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        assessmentDto.setTopicConjunctions(topicConjunctionList)
        assessmentDto = assessmentService.createAssessment(courseExecution.getId(), assessmentDto)

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)
        tournamentDto = tournamentService.createTournament(user.getId(), courseExecution.getId(), topics, tournamentDto)

        question1 = new Question()
        question1.setKey(1)
        question1.setCreationDate(LOCAL_DATE_TODAY)
        question1.setContent(QUESTION_1_CONTENT)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setStatus(Question.Status.AVAILABLE)
        question1.setCourse(course)
        question1.addTopic(topic1)
        topic1.addQuestion(question1)

        def questionDetails1 = new MultipleChoiceQuestion()
        question1.setQuestionDetails(questionDetails1)

        questionRepository.save(question1)
        topicRepository.save(topic1)

        def optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails1)
        optionRepository.save(optionOK)
    }

    def "user solves quiz tournament"() {
        given: 'user joins tournament'
        tournamentService.joinTournament(user.getId(), tournamentDto.getId(), "")

        when:
        response = restClient.put(
                path: '/tournaments/' + courseExecution.getId() + '/solveQuiz/' + tournamentDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        cleanup:
        tournamentRepository.delete(tournamentRepository.findById(tournamentDto.getId()).get())
        assessmentRepository.delete(assessmentRepository.findById(assessmentDto.getId()).get())
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}