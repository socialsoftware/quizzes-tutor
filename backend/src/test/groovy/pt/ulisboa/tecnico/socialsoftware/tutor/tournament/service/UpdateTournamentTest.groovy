package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateTournamentTest extends TournamentTest {
    def topic3
    def tournamentDto
    def user2

    def setup() {
        user2 = createUser(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, externalCourseExecution)

        def topicDto3 = new TopicDto()
        topicDto3.setName(TOPIC_3_NAME)
        topic3 = new Topic(externalCourse, topicDto3)
        topicRepository.save(topic3)

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)

        createAssessmentWithTopicConjunction(ASSESSMENT_1_TITLE, Assessment.Status.AVAILABLE, externalCourseExecution)

        def question1 = createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)

        createOption(OPTION_1_CONTENT, question1)
    }

    def "user that created tournament changes start time"() {
        given:
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "new startTime"
        def newStartTime = STRING_DATE_TOMORROW_PLUS_10_MINUTES
        tournamentDto.setStartTime(newStartTime)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getStartTime()) == newStartTime
    }

    def "user that created tournament changes end time"() {
        given:
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "new endTime"
        def newEndTime = STRING_DATE_LATER_PLUS_10_MINUTES
        tournamentDto.setEndTime(newEndTime)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getEndTime()) == newEndTime
    }

    def "user that created tournament changes number of questions"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "a new number of questions"
        def newNumberOfQuestions = 10
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.numberOfQuestions == newNumberOfQuestions
    }

    def "user that created tournament adds topic of same course"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "a new topics list"
        topics.add(topic3.getId())

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopics() == [topic2, topic3, topic1]  as Set
    }

    def "user that created tournament adds topic of different course"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "new course"
        def differentCourse = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(differentCourse)
        and: "a new topics list"
        topic3.setCourse(differentCourse)
        topics.add(topic3.getId())

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_TOPIC_COURSE
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopics() == [topic2, topic1]  as Set
    }

    def "user that created tournament removes existing topic from tournament that contains that topic"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "a new topics list"
        topics.remove(topic2.getId())

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopics() == [topic1] as Set
    }

    def "user that created an open tournament tries to change it"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that created tournament tries to change it after has ended with no answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "a new number of questions"
        def newNumberOfQuestions = 10
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.numberOfQuestions == newNumberOfQuestions
    }

    def "user that created tournament tries to change it with answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "join a tournament"
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        and: "solve a tournament"
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())
        and: "is now closed"
        tournamentDto.setEndTime(STRING_DATE_TODAY)

        when:
        tournamentService.updateTournament(topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
