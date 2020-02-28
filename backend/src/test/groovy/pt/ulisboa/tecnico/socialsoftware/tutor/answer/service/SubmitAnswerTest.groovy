//package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
//import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
//import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
//import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
//import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
//import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
//import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
//import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
//import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
//import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
//import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
//import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
//import spock.lang.Specification
//
//import java.time.LocalDateTime
//
//import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*
//
//@DataJpaTest
//class SubmitAnswer extends Specification {
//    public static final String COURSE_NAME = "Software Architecture"
//    public static final String ACRONYM = "AS1"
//    public static final String ACADEMIC_TERM = "1 SEM"
//
//    @Autowired
//    AnswerService answerService
//
//    @Autowired
//    UserRepository userRepository
//
//    @Autowired
//    CourseRepository courseRepository
//
//    @Autowired
//    CourseExecutionRepository courseExecutionRepository
//
//    @Autowired
//    QuizRepository quizRepository
//
//    @Autowired
//    QuizQuestionRepository quizQuestionRepository
//
//    @Autowired
//    QuizAnswerRepository quizAnswerRepository
//
//    @Autowired
//    QuestionRepository questionRepository
//
//    @Autowired
//    OptionRepository optionRepository
//
//    @Autowired
//    QuestionAnswerRepository questionAnswerRepository
//
//    def user
//    def courseExecution
//    def quizQuestion
//    def optionOk
//    def optionKO
//    def quizAnswer
//    def date
//
//    def setup() {
//        def course = new Course(COURSE_NAME)
//        courseRepository.save(course)
//
//        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM)
//        courseExecutionRepository.save(courseExecution)
//
//        user = new User('name', "username", 1, User.Role.STUDENT)
//        user.getCourseExecutions().add(courseExecution)
//        courseExecution.getUsers().add(user)
//
//        def quiz = new Quiz()
//        quiz.setKey(1)
//        quiz.setType(Quiz.QuizType.GENERATED)
//        quiz.setCourseExecution(courseExecution)
//        courseExecution.addQuiz(quiz)
//
//        quizAnswer = new QuizAnswer(user, quiz)
//
//        def question = new Question()
//        question.setKey(1)
//        question.setCourse(course)
//        course.addQuestion(question)
//
//        quizQuestion = new QuizQuestion(quiz, question, 0)
//        optionKO = new Option()
//        optionKO.setCorrect(false)
//        question.addOption(optionKO)
//        optionOk = new Option()
//        optionOk.setCorrect(true)
//        question.addOption(optionOk)
//
//        date = LocalDateTime.now()
//
//        userRepository.save(user)
//        quizRepository.save(quiz)
//        questionRepository.save(question)
//        quizQuestionRepository.save(quizQuestion)
//        quizAnswerRepository.save(quizAnswer)
//        optionRepository.save(optionOk)
//        optionRepository.save(optionKO)
//    }
//
//    def 'create for one question and two options'() {
//        given:
//        def resultsDto = new ArrayList<StatementAnswerDto>()
//        def resultDto = new StatementAnswerDto()
//        resultDto.setQuizQuestionId(quizQuestion.getId())
//        resultDto.setOptionId(optionOk.getId())
//        resultsDto.add(resultDto)
//        def resultAnswersDto = new ResultAnswersDto()
//        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        resultAnswersDto.setAnswerDate(date)
//        resultAnswersDto.setAnswers(resultsDto)
//
//        when:
//        def correctAnswersDto = answerService.concludeQuiz(user, quizId)
//
//        then: 'the value is createQuestion and persistent'
//        quizAnswer.isCompleted()
//        quizAnswer.getAnswerDate() == date
//        questionAnswerRepository.findAll().size() == 1
//        def result = questionAnswerRepository.findAll().get(0)
//        result.getQuizAnswer() == quizAnswer
//        quizAnswer.getQuestionAnswers().contains(result)
//        result.getQuizQuestion() == quizQuestion
//        quizQuestion.getQuestionAnswers().contains(result)
//        result.getOption() == optionOk
//        optionOk.getQuestionAnswers().contains(result)
//        and: 'the return value is OK'
//        correctAnswersDto.getAnswers().size() == 1
//        def correctAnswerDto = correctAnswersDto.getAnswers().get(0)
//        correctAnswerDto.getQuizQuestionId() == quizQuestion.getId()
//        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
//    }
//
//    def 'user not consistent'() {
//        given:
//        def resultsDto = new ArrayList<StatementAnswerDto>()
//        def resultDto = new StatementAnswerDto()
//        resultDto.setQuizQuestionId(quizQuestion.getId())
//        resultDto.setOptionId(optionOk.getId())
//        resultsDto.add(resultDto)
//        def resultAnswersDto = new ResultAnswersDto()
//        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        resultAnswersDto.setAnswers(resultsDto)
//        and: 'another user'
//        def otherUser = new User('name', "usernam2", 2, User.Role.STUDENT)
//        user.getCourseExecutions().add(courseExecution)
//        courseExecution.getUsers().add(user)
//        userRepository.save(otherUser)
//
//
//        when:
//        answerService.concludeQuiz(otherUser, resultAnswersDto)
//
//        then:
//        TutorException exception = thrown()
//        exception.getErrorMessage() == QUIZ_USER_MISMATCH
//        questionAnswerRepository.findAll().size() == 0
//    }
//
//    def 'quiz question not consistent'() {
//        given:
//        def resultsDto = new ArrayList<StatementAnswerDto>()
//        def resultDto = new StatementAnswerDto()
//        def quiz = new Quiz()
//        quiz.setKey(2)
//        def question = new Question()
//        question.setKey(2)
//        def quizQuestion = new QuizQuestion(quiz, question, 0)
//        quizRepository.save(quiz)
//        questionRepository.save(question)
//        quizQuestionRepository.save(quizQuestion)
//        resultDto.setQuizQuestionId(quizQuestion.getId())
//        resultDto.setOptionId(optionOk.getId())
//        resultsDto.add(resultDto)
//        def resultAnswersDto = new ResultAnswersDto()
//        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        resultAnswersDto.setAnswers(resultsDto)
//
//        when:
//        answerService.concludeQuiz(user, resultAnswersDto)
//
//        then:
//        TutorException exception = thrown()
//        exception.getErrorMessage() == QUIZ_MISMATCH
//        questionAnswerRepository.findAll().size() == 0
//    }
//
//    def 'question option not consistent'() {
//        given:
//        def resultsDto = new ArrayList<StatementAnswerDto>()
//        def resultDto = new StatementAnswerDto()
//        resultDto.setQuizQuestionId(quizQuestion.getId())
//        def otherOptionOK = new Option()
//        otherOptionOK.setCorrect(true)
//        def otherQuestion = new Question()
//        otherQuestion.setKey(2)
//        otherQuestion.addOption(otherOptionOK)
//        questionRepository.save(otherQuestion)
//        optionRepository.save(otherOptionOK)
//        resultDto.setOptionId(otherOptionOK.getId())
//        resultsDto.add(resultDto)
//        def resultAnswersDto = new ResultAnswersDto()
//        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        resultAnswersDto.setAnswers(resultsDto)
//
//        when:
//        answerService.concludeQuiz(user, resultAnswersDto)
//
//        then:
//        TutorException exception = thrown()
//        exception.getErrorMessage() == QUIZ_OPTION_MISMATCH
//        questionAnswerRepository.findAll().size() == 0
//    }
//
//
//    def 'double submition of the same answers'() {
//        given:
//        def resultsDto = new ArrayList<StatementAnswerDto>()
//        def resultDto = new StatementAnswerDto()
//        resultDto.setQuizQuestionId(quizQuestion.getId())
//        resultDto.setOptionId(optionOk.getId())
//        resultsDto.add(resultDto)
//        def resultAnswersDto = new ResultAnswersDto()
//        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        resultAnswersDto.setAnswerDate(date)
//        resultAnswersDto.setAnswers(resultsDto)
//        and: "Submit answer for the first time"
//        answerService.concludeQuiz(user, resultAnswersDto)
//
//        and: "Second submission with different option"
//        def secondResultsDto = new ArrayList<StatementAnswerDto>()
//        def secondResultDto = new StatementAnswerDto()
//        secondResultDto.setQuizQuestionId(quizQuestion.getId())
//        secondResultDto.setOptionId(optionKO.getId())
//        secondResultsDto.add(secondResultDto)
//        def secondResultAnswersDto = new ResultAnswersDto()
//        secondResultAnswersDto.setQuizAnswerId(quizAnswer.getId())
//        secondResultAnswersDto.setAnswerDate(date)
//        secondResultAnswersDto.setAnswers(secondResultsDto)
//
//        when: "Second submission"
//        def correctAnswersDto = answerService.concludeQuiz(user, secondResultAnswersDto)
//
//        then: 'the value is createQuestion and persistent'
//        quizAnswer.isCompleted()
//        quizAnswer.getAnswerDate() == date
//        questionAnswerRepository.findAll().size() == 1
//        def result = questionAnswerRepository.findAll().get(0)
//        result.getQuizAnswer() == quizAnswer
//        quizAnswer.getQuestionAnswers().contains(result)
//        result.getQuizQuestion() == quizQuestion
//        quizQuestion.getQuestionAnswers().contains(result)
//        result.getOption() == optionOk
//        optionOk.getQuestionAnswers().contains(result)
//        and: 'the return value is OK'
//        correctAnswersDto.getAnswers().size() == 1
//        def correctAnswerDto = correctAnswersDto.getAnswers().get(0)
//        correctAnswerDto.getQuizQuestionId() == quizQuestion.getId()
//        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
//    }
//
//    @TestConfiguration
//    static class AnswerServiceImplTestContextConfiguration {
//
//        @Bean
//        AnswerService answerService() {
//            return new AnswerService()
//        }
//    }
//}
