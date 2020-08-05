package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class QuizTournamentGenerateTest extends SpockTest {
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
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)
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

    def "1 student join a tournament" () {
        given:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")

        when:
        tournamentService.solveQuiz(user1.getId(), tournamentDto)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == ("Tournament " + tournamentDto.getId() + " Quiz")
        result.getType() == Quiz.QuizType.TOURNAMENT
        DateHandler.toISOString(result.getConclusionDate()) == STRING_DATE_LATER
        result.getQuizQuestions().size() == NUMBER_OF_QUESTIONS
    }

    def "2 student join a tournament" () {
        given:
        def user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user2.addCourse(courseExecution)
        userRepository.save(user2)

        and:
        tournamentService.joinTournament(user1.getId(), tournamentDto, "")
        tournamentService.joinTournament(user2.getId(), tournamentDto, "")

        when: 'both students solve the quiz'
        tournamentService.solveQuiz(user1.getId(), tournamentDto)
        tournamentService.solveQuiz(user2.getId(), tournamentDto)

        then: 'there is only one quiz generated'
        quizRepository.count() == 1L

        and: "the correct quiz is inside the repository"
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == ("Tournament " + tournamentDto.getId() + " Quiz")
        result.getType() == Quiz.QuizType.TOURNAMENT
        DateHandler.toISOString(result.getConclusionDate()) == STRING_DATE_LATER
        result.getQuizQuestions().size() == NUMBER_OF_QUESTIONS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}