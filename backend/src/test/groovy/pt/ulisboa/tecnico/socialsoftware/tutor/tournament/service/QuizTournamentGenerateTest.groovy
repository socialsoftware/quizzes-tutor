package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class QuizTournamentGenerateTest extends TournamentTest {
    def tournamentDto
    def question

    def setup() {
        tournamentDto = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        createAssessmentWithTopicConjunction(ASSESSMENT_1_TITLE, Assessment.Status.AVAILABLE, externalCourseExecution)

        question = createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)
    }

    def "generate a quiz with 1 student solving" () {
        given:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")

        when:
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == ("Tournament " + tournamentDto.getId() + " Quiz")
        result.getType() == Quiz.QuizType.TOURNAMENT
        DateHandler.toISOString(result.getConclusionDate()) == STRING_DATE_LATER
        result.getQuizQuestionsNumber() == NUMBER_OF_QUESTIONS
    }

    def "generate a quiz with 1 student solving and question with less one topic" () {
        given: 'a participant'
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        and: 'remove topic from question'
        question.getTopics().remove(topic2)
        topic2.getQuestions().remove(question)

        when:
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == NOT_ENOUGH_QUESTIONS_TOURNAMENT
        quizRepository.count() == 0L
    }

    def "generate a quiz with 1 student solving and question with more one topic" () {
        given: 'a participant'
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        and: 'a new topic'
        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_3_NAME)
        def topic3 = new Topic(externalCourse, topicDto1)
        topicRepository.save(topic3)
        and: 'add topic to question'
        question.addTopic(topic3)

        when:
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == NOT_ENOUGH_QUESTIONS_TOURNAMENT
        quizRepository.count() == 0L
    }

    def "generate a quiz with 2 student solving" () {
        given:
        def user2 = createUser(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, externalCourseExecution)
        and:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user2, "")

        when: 'both students solve the quiz'
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())
        tournamentService.solveQuiz(user2.getId(), tournamentDto.getId())

        then: 'there is only one quiz generated'
        quizRepository.count() == 1L
        and: "the correct quiz is inside the repository"
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == ("Tournament " + tournamentDto.getId() + " Quiz")
        result.getType() == Quiz.QuizType.TOURNAMENT
        DateHandler.toISOString(result.getConclusionDate()) == STRING_DATE_LATER
        result.getQuizQuestionsNumber() == NUMBER_OF_QUESTIONS
    }

    def "disabling assessment for already created tournament" () {
        given:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        and:
        assessment.setStatus(Assessment.Status.DISABLED)

        when:
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == NOT_ENOUGH_QUESTIONS_TOURNAMENT
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}