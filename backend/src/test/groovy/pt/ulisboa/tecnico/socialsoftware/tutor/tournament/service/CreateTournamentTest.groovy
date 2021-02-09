package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateTournamentTest extends TournamentTest {
    def tournamentDto

    def setup() {
        tournamentDto = new TournamentDto()
    }

    @Unroll
    def "valid arguments: startTime=#startTime | endTime=#endTime | numberOfQuestions=#numberOfQuestions"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)
        tournamentDto.setNumberOfQuestions(numberOfQuestions)
        tournamentDto.setCanceled(false)

        when:
        tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        DateHandler.toISOString(result.getStartTime()) == startTime
        DateHandler.toISOString(result.getEndTime()) == endTime
        result.getTopics() == [topic2, topic1] as Set
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.isCanceled() == false
        result.getCreator() == user1
        result.getCourseExecution() == externalCourseExecution

        where:
        startTime            | endTime              | numberOfQuestions
        STRING_DATE_TODAY    | STRING_DATE_TOMORROW | NUMBER_OF_QUESTIONS
        STRING_DATE_TOMORROW | STRING_DATE_LATER    | NUMBER_OF_QUESTIONS
    }

    def "create a private tournament"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)
        tournamentDto.setPrivateTournament(true)
        tournamentDto.setPassword('123')

        when:
        tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        DateHandler.toISOString(result.getStartTime()) == STRING_DATE_TODAY
        DateHandler.toISOString(result.getEndTime()) == STRING_DATE_LATER
        result.getTopics() == [topic2, topic1] as Set
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.isCanceled() == false
        result.getCreator() == user1
        result.getCourseExecution() == externalCourseExecution
        result.isPrivateTournament() == true
        result.getPassword() == '123'
    }

    def createTournament(String userType, String topicsType) {
        def userId
        if (userType == 'good one') {
            userId = user1.getId()
        }
        else if (userType != null) {
            userId = Integer.parseInt(userType)
        }

        def topicsList
        if (topicsType == 'good one') {
            topicsList = topics
        }
        else if (topicsType == 'empty') {
            topicsList = [] as Set
        }
        else if (topicsType == 'missing topic') {
            topicsList = [-1] as Set
        }

        tournamentService.createTournament(userId, externalCourseExecution.getId(), topicsList, tournamentDto)
    }

    @Unroll
    def "invalid arguments: userType=#userType | startTime=#startTime | endTime=#endTime | numberOfQuestions=#numberOfQuestions | topicsType=#topicsType || errorMessage=#errorMessage"() {
        given: 'a tournamentDto'
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)
        tournamentDto.setNumberOfQuestions(numberOfQuestions)
        tournamentDto.setCanceled(false)

        when:
        createTournament(userType, topicsType)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        tournamentRepository.count() == 0L

        where:
        userType   | startTime             | endTime           | numberOfQuestions   | topicsType      || errorMessage
        null       | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_MISSING_USER
        "99"       | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "good one"      || USER_NOT_FOUND
        "good one" | null                  | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_MISSING_START_TIME
        "good one" | " "                   | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | "bad"                 | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_YESTERDAY | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TOMORROW  | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_LATER     | STRING_DATE_TODAY | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TODAY     | null              | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_MISSING_END_TIME
        "good one" | STRING_DATE_TODAY     | " "               | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TODAY     | "bad"             | NUMBER_OF_QUESTIONS | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | null                | "good one"      || TOURNAMENT_MISSING_NUMBER_OF_QUESTIONS
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | 0                   | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | -1                  | "good one"      || TOURNAMENT_NOT_CONSISTENT
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "null"          || TOURNAMENT_MISSING_TOPICS
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "empty"         || TOURNAMENT_MISSING_TOPICS
        "good one" | STRING_DATE_TODAY     | STRING_DATE_LATER | NUMBER_OF_QUESTIONS | "missing topic" || TOPIC_NOT_FOUND
    }

    def "create tournament with existing user and topics from different courses"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)
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
        tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_TOPIC_COURSE
        tournamentRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
