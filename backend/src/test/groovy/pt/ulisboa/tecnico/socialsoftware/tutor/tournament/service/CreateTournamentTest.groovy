package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateTournamentTest extends SpockTest {
    def topic1
    def topic2
    def tournamentDto
    def topics = new HashSet<Integer>()
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

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

        tournamentDto = new TournamentDto()
    }

    @Unroll
    def "valid arguments: userRole=#userRole | startTime=#startTime | endTime=#endTime | numberOfQuestions=#numberOfQuestions"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)
        tournamentDto.setNumberOfQuestions(numberOfQuestions)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        and: 'a user'
        user.setRole(userRole)

        when:
        tournamentService.createTournament(user.getId(), topics, tournamentDto)

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        DateHandler.toISOString(result.getStartTime()) == startTime
        DateHandler.toISOString(result.getEndTime()) == endTime
        result.getTopicConjunction().getTopics() == [topic2, topic1] as Set
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getState() == Tournament.Status.NOT_CANCELED
        result.getCreator() == user
        result.getCourseExecution() == courseExecution

        where:
        userRole          | startTime            | endTime              | numberOfQuestions
        User.Role.STUDENT | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | NUMBER_OF_QUESTIONS
        User.Role.STUDENT | STRING_DATE_TOMORROW | STRING_DATE_LATER    | NUMBER_OF_QUESTIONS
    }

    def "create a private tournament"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)
        tournamentDto.setPrivateTournament(true)
        tournamentDto.setPassword('123')

        when:
        tournamentService.createTournament(user.getId(), topics, tournamentDto)

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        DateHandler.toISOString(result.getStartTime()) == STRING_DATE_TODAY
        DateHandler.toISOString(result.getEndTime()) == STRING_DATE_LATER
        result.getTopicConjunction().getTopics() == [topic2, topic1] as Set
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getState() == Tournament.Status.NOT_CANCELED
        result.getCreator() == user
        result.getCourseExecution() == courseExecution
        result.isPrivateTournament() == true
        result.getPassword() == '123'
    }

    @Unroll
    def "invalid arguments: userRole=#userRole | startTime=#startTime | endTime=#endTime | numberOfQuestions=#numberOfQuestions || errorMessage=#errorMessage"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)
        tournamentDto.setNumberOfQuestions(numberOfQuestions)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        and: 'a user'
        user.setRole(userRole)

        when:
        tournamentService.createTournament(user.getId(), topics, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        tournamentRepository.count() == 0L

        where:
        userRole          | startTime             | endTime           | numberOfQuestions   || errorMessage
        User.Role.ADMIN   | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS || USER_NOT_STUDENT
        User.Role.TEACHER | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS || USER_NOT_STUDENT
        User.Role.STUDENT | null                  | STRING_DATE_LATER | NUMBER_OF_QUESTIONS || TOURNAMENT_MISSING_START_TIME
        User.Role.STUDENT | " "                   | STRING_DATE_LATER | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | "bad"                 | STRING_DATE_LATER | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_TODAY     | null              | NUMBER_OF_QUESTIONS || TOURNAMENT_MISSING_END_TIME
        User.Role.STUDENT | STRING_DATE_TODAY     | " "               | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_TODAY     | "bad"             | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_TODAY     | STRING_DATE_LATER | null                || TOURNAMENT_MISSING_NUMBER_OF_QUESTIONS
        User.Role.STUDENT | STRING_DATE_TODAY     | STRING_DATE_LATER | 0                   || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_TODAY     | STRING_DATE_LATER | -1                  || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_YESTERDAY | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_TOMORROW  | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | STRING_DATE_LATER     | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS || TOURNAMENT_NOT_CONSISTENT
    }

    def "create tournament with null user"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        when:
        tournamentService.createTournament(null, topics, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_MISSING_USER
        tournamentRepository.count() == 0L
    }

    def "create tournament with not existing user"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        and:
        def fakeUserId = 99

        when:
        tournamentService.createTournament(fakeUserId, topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
        tournamentRepository.count() == 0L
    }

    def "create tournament with null topics"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        when:
        tournamentService.createTournament(user.getId(), null, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_MISSING_TOPICS
        tournamentRepository.count() == 0L
    }

    def "create tournament with zero topics"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        when:
        tournamentService.createTournament(user.getId(), [] as Set, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_MISSING_TOPICS
        tournamentRepository.count() == 0L
    }

    def "create tournament with non existing topic"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        when:
        tournamentService.createTournament(user.getId(), [-1] as Set, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOPIC_NOT_FOUND
        tournamentRepository.count() == 0L
    }

    def "create tournament with existing user and topics from different courses"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)

        and: "new course"
        def differentCourse = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(differentCourse)

        and: "new topic"
        def topicDto3 = new TopicDto()
        topicDto3.setName("TOPIC3")
        def topic3 = new Topic(differentCourse, topicDto3)
        topicRepository.save(topic3)
        topics.add(topic3.getId())

        when:
        tournamentService.createTournament(user.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_TOPIC_COURSE
        tournamentRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
