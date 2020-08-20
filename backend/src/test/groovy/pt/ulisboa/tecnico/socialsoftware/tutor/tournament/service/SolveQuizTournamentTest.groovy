package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class SolveQuizTournamentTest extends SpockTest {
    public static final String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())

    def question1
    def topic1
    def topic2
    def tournamentDto
    def topics = new HashSet<Integer>()
    def user1

    def setup() {
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user1.addCourse(courseExecution)
        userRepository.save(user1)
        user1.setKey(user1.getId())

        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(course, topicDto1)
        topicRepository.save(topic1)

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_2_NAME)
        topic2 = new Topic(course, topicDto2)
        topicRepository.save(topic2)

        topics.add(topic1.getId())
        topics.add(topic2.getId())

        def assessment = new Assessment()
        assessment.setTitle(ASSESSMENT_1_TITLE)
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setCourseExecution(courseExecution)

        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic1)
        topicConjunction.addTopic(topic2)

        assessment.addTopicConjunction(topicConjunction)
        assessmentRepository.save(assessment)

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(false)
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        question1 = new Question()
        question1.setKey(1)
        question1.setCreationDate(LOCAL_DATE_TODAY)
        question1.setContent(QUESTION_1_CONTENT)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setStatus(Question.Status.AVAILABLE)
        question1.setCourse(course)
        question1.addTopic(topic1)
        question1.addTopic(topic2)
        questionRepository.save(question1)
    }

    def "1 student join a tournament and solves it" () {
        given:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        when:
        def result = tournamentService.solveQuiz(user1.getId(), tournamentDto)

        then: "solved it"
        result != null
    }

    def "2 student join a tournament and solve it" () {
        given:
        def user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user2.addCourse(courseExecution)
        userRepository.save(user2)

        and:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        when:
        def result1 = tournamentService.solveQuiz(user1.getId(), tournamentDto)
        def result2 = tournamentService.solveQuiz(user2.getId(), tournamentDto)

        then: "solved it"
        result1 != null
        result2 != null
    }

    def "student that did not join tries to solve tournament" () {
        given:

        when:
        def result = tournamentService.solveQuiz(user1.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_NOT_JOINED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}