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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CancelTournamentTest extends SpockTest {
    public static final String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())

    def question1
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
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(false)

        question1 = new Question()
        question1.setKey(1)
        question1.setCreationDate(LOCAL_DATE_TODAY)
        question1.setContent(QUESTION_1_CONTENT)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setStatus(Question.Status.AVAILABLE)
        question1.setCourse(course)
        question1.addTopic(topic1)
        question1.addTopic(topic2)

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        question1.setOptions(options)

        questionRepository.save(question1)
    }

    def "user that created tournament cancels it"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.isCanceled()
    }

    def "user that created an open tournament tries to cancel it"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that created tournament tries to cancel it after has ended with no answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.isCanceled()
    }

    def "user that created tournament tries to cancel it with answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        and: "join a tournament"
        tournamentService.joinTournament(user.getId(), tournamentDto, "")

        and: "solve a tournament"
        tournamentService.solveQuiz(user.getId(), tournamentDto)

        and: "is now closed"
        tournamentDto.setEndTime(STRING_DATE_TODAY)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that did not created tournament cancels it"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        and: "a new user"
        def user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        courseExecution.addUser(user2)
        courseExecutionRepository.save(courseExecution)

        user2.addCourse(courseExecution)
        userRepository.save(user2)

        when:
        tournamentService.cancelTournament(user2.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR

        and:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        !result.isCanceled()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
