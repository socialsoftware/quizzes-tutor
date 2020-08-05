package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
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
class JoinTournamentTest extends SpockTest {
    def question1
    def topic1
    def topic2
    def tournamentDto
    def privateTournamentDto
    def topics = new HashSet<Integer>()
    def user1
    def user2

    def setup() {
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)

        user1.addCourse(courseExecution)
        userRepository.save(user1)
        user1.setKey(user1.getId())

        user2.addCourse(courseExecution)
        userRepository.save(user2)
        user2.setKey(user2.getId())

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

        privateTournamentDto = new TournamentDto()
        privateTournamentDto.setStartTime(STRING_DATE_TODAY)
        privateTournamentDto.setEndTime(STRING_DATE_LATER)
        privateTournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        privateTournamentDto.setState(false)
        privateTournamentDto.setPrivateTournament(true)
        privateTournamentDto.setPassword('123')
        privateTournamentDto = tournamentService.createTournament(user1.getId(), topics, privateTournamentDto)

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

    def "2 student join an open tournament and get participants" () {
        given:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        when:
        def result = tournamentService.getTournamentParticipants(tournamentDto)

        then: "the students have joined the tournament"
        result.size() == 2
        def resTournamentParticipant1 = result.get(0)
        def resTournamentParticipant2 = result.get(1)

        resTournamentParticipant1.getName() == USER_1_NAME || USER_2_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME || USER_2_USERNAME

        resTournamentParticipant2.getName() == USER_1_NAME || USER_2_NAME
        resTournamentParticipant2.getUsername() == USER_1_USERNAME || USER_2_USERNAME

        resTournamentParticipant1 != resTournamentParticipant2
    }

    def "1 student and 1 teacher join an open tournament and get participants" () {
        given: 'a teacher'
        user2.setRole(User.Role.TEACHER)

        and:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        when:
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        then: "the teacher cannot join the tournament"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_STUDENT

        and: "the student have joined the tournament"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 1
        def resTournamentParticipant1 = result.get(0)

        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "1 student and 1 admin join an open tournament and get participants" () {
        given: 'an admin'
        user2.setRole(User.Role.ADMIN)

        and:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        when:
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        then: "the admin cannot join the tournament"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_STUDENT

        and: "the student have joined the tournament"
        def result = tournamentService.getTournamentParticipants(tournamentDto)

        result.size() == 1
        def resTournamentParticipant1 = result.get(0)

        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "1 teacher join an open tournament and get participants" () {
        given: 'a teacher'
        user2.setRole(User.Role.TEACHER)

        when:
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        then: "the teacher cannot join the tournament"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_STUDENT

        and: "the tournament has no participants"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 0
    }

    def "1 admin join an open tournament and get participants" () {
        given: 'an admin'
        user2.setRole(User.Role.ADMIN)

        when:
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        then: "the admin cannot join the tournament"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_STUDENT

        and: "the tournament has no participants"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 0
    }

    def "Student tries to join canceled tournament" () {
        given: 'a canceled tournament'
        def canceledTournamentDto = new TournamentDto()
        canceledTournamentDto.setStartTime(STRING_DATE_TODAY)
        canceledTournamentDto.setEndTime(STRING_DATE_LATER)
        canceledTournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        canceledTournamentDto.setState(true)
        canceledTournamentDto = tournamentService.createTournament(user1.getId(), topics, canceledTournamentDto)

        when:
        tournamentService.joinTournament(user1.getId(), canceledTournamentDto, "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CANCELED

        and: "the tournament has no participants"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 0
    }

    def "Student tries to join not open tournament" () {
        given: 'a not open tournament'
        def notOpenTournamentDto = new TournamentDto()
        notOpenTournamentDto.setStartTime(STRING_DATE_TODAY)
        notOpenTournamentDto.setEndTime(STRING_DATE_TODAY)
        notOpenTournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        notOpenTournamentDto.setState(true)
        notOpenTournamentDto = tournamentService.createTournament(user1.getId(), topics, notOpenTournamentDto)

        when:
        tournamentService.joinTournament(user2.getId(), notOpenTournamentDto, "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_OPEN

        and: "the tournament has no participants"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 0
    }

    def "Student tries to join tournament twice" () {
        given:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        when:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DUPLICATE_TOURNAMENT_PARTICIPANT

        and: "the tournament has 1 participant"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 1

        def resTournamentParticipant1 = result.get(0)

        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "Non-existing student tries to join canceled tournament" () {
        given: 'a non-existing user'
        def fakeUserId = 99

        when:
        tournamentService.joinTournament(fakeUserId, tournamentDto, "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND

        and: "the tournament has no participants"
        def result = tournamentService.getTournamentParticipants(tournamentDto)
        result.size() == 0
    }

    def "student joins an open and private tournament with correct password" () {
        given:
        tournamentService.joinTournament(user1.getId(), privateTournamentDto, "123")

        when:
        def result = tournamentService.getTournamentParticipants(privateTournamentDto)

        then: "the students have joined the tournament"
        result.size() == 1
        def resTournamentParticipant1 = result.get(0)

        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "student joins an open and private tournament with wrong password" () {
        given:

        when:
        tournamentService.joinTournament(user2.getId(), privateTournamentDto, "Not 123")

        then: "receives exeption"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == WRONG_TOURNAMENT_PASSWORD

        and: "tournament has no participants"
        def result = tournamentService.getTournamentParticipants(privateTournamentDto)
        result.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
