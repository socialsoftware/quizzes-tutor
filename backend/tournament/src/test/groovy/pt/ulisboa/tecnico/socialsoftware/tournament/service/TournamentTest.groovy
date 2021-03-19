package pt.ulisboa.tecnico.socialsoftware.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.dtos.execution.CourseExecutionStatus
import pt.ulisboa.tecnico.socialsoftware.dtos.question.OptionDto
import pt.ulisboa.tecnico.socialsoftware.dtos.question.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tournament.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tournament.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.utils.DateHandler

import java.time.LocalDateTime

@DataJpaTest
class TournamentTest extends SpockTest {
    public static String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())

    def assessment
    def topic1
    def topic2
    def tournamentTopic1
    def tournamentTopic2
    def topics = new HashSet<Integer>()
    def topicsList = new HashSet<TournamentTopic>()
    def user1
    def creator1
    def tournamentExternalCourseExecution
    def participant1

    def setup() {
        user1 = createUser(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, externalCourseExecution)
        creator1 = createTournamentCreator(user1)
        participant1 = createTournamentParticipant(user1)

        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(externalCourse, topicDto1)
        topicRepository.save(topic1)
        tournamentTopic1 = createTournamentTopic(topic1)

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_2_NAME)
        topic2 = new Topic(externalCourse, topicDto2)
        topicRepository.save(topic2)
        tournamentTopic2 = createTournamentTopic(topic2)

        topics.add(tournamentTopic1.getId())
        topics.add(tournamentTopic2.getId())

        topicsList.add(tournamentTopic1)
        topicsList.add(tournamentTopic2)

        STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())

        tournamentExternalCourseExecution = createTournamentCourseExecution(externalCourseExecution)
    }

    def createUser(String name, String username, String email, User.Role role, CourseExecution courseExecution) {
        def user = new User(name, username, email, role, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        userRepository.save(user)

        return user
    }

    def createTournament(TournamentCreator creator, String startTime, String endTime, Integer numberOfQuestions, boolean isCanceled) {
        def tournament = new Tournament()
        tournament.setStartTime(DateHandler.toLocalDateTime(startTime))
        tournament.setEndTime(DateHandler.toLocalDateTime(endTime))
        tournament.setNumberOfQuestions(numberOfQuestions)
        tournament.setCanceled(isCanceled)
        tournament.setCreator(creator)
        tournament.setCourseExecution(tournamentExternalCourseExecution)
        tournament.setTopics(topicsList)
        tournament.setPassword('')
        tournament.setPrivateTournament(false)
        tournamentRepository.save(tournament)

        return tournament.getDto()
    }

    def createPrivateTournament(TournamentCreator creator, String startTime, String endTime, Integer numberOfQuestions, boolean isCanceled, String password) {
        def tournament = new Tournament()
        tournament.setStartTime(DateHandler.toLocalDateTime(startTime))
        tournament.setEndTime(DateHandler.toLocalDateTime(endTime))
        tournament.setNumberOfQuestions(numberOfQuestions)
        tournament.setCanceled(isCanceled)
        tournament.setCreator(creator)
        tournament.setCourseExecution(tournamentExternalCourseExecution)
        tournament.setTopics(topicsList)
        tournament.setPassword(password)
        tournament.setPrivateTournament(true)
        tournamentRepository.save(tournament)

        return tournament.getDto()
    }

    def createAssessmentWithTopicConjunction(String title, Assessment.Status status, CourseExecution courseExecution) {
        assessment = new Assessment()
        assessment.setTitle(title)
        assessment.setStatus(status)
        assessment.setCourseExecution(courseExecution)

        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic1)
        topicConjunction.addTopic(topic2)
        topicConjunction.setAssessment(assessment)
        assessment.addTopicConjunction(topicConjunction)

        assessmentRepository.save(assessment)
    }

    def createMultipleChoiceQuestion(LocalDateTime creationDate, String content, String title, Question.Status status, Course course) {
        def question = new Question()
        question.setKey(1)
        question.setCreationDate(creationDate)
        question.setContent(content)
        question.setTitle(title)
        question.setStatus(status)
        question.setCourse(course)
        question.addTopic(topic1)
        question.addTopic(topic2)

        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)

        questionRepository.save(question)

        return question
    }

    def createOption(String content, Question question) {
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        question.getQuestionDetails().setOptions(options)
        questionRepository.save(question)
    }

    def createTournamentCreator(User user) {
        return new TournamentCreator(user.getId(), user.getUsername(), user.getName())
    }

    def createTournamentTopic(Topic topic) {
        return new TournamentTopic(topic.getId(), topic.getName(), topic.getCourse().getId())
    }

    def createTournamentCourseExecution(CourseExecution courseExecution) {
        return new TournamentCourseExecution(courseExecution.getId(),
                courseExecution.getCourse().getId(),
                CourseExecutionStatus.valueOf(courseExecution.getStatus().toString()),
                courseExecution.getAcronym())
    }

    def createTournamentParticipant(User user) {
        return new TournamentParticipant(user.getId(), user.getUsername(), user.getName())
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
