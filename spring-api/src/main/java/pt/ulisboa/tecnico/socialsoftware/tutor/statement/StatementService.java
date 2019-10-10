package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.NOT_ENOUGH_QUESTIONS;

@Service
public class StatementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public StatementQuizDto generateStudentQuiz(String username, int quizSize) {
        User user = userRepository.findByUsername(username);

        Quiz quiz = new Quiz();
        quiz.setNumber(quizService.getMaxQuizNumber() + 1);

        List<Question> availableQuestions = questionRepository.getAvailableQuestions();

        if (availableQuestions.size() < quizSize) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        // TODO: to include knowhow about the student in the future
        quiz.generate(quizSize, availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        entityManager.persist(quiz);
        entityManager.persist(quizAnswer);

        return new StatementQuizDto(quizAnswer);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<StatementQuizDto> getAvailableQuizzes(String username) {
        User user = userRepository.findByUsername(username);

        LocalDateTime now = LocalDateTime.now();

        Set<Integer> studentQuizIds =  user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        quizRepository.findAvailableTeacherQuizzes(user.getYear()).stream()
                .filter(quiz -> quiz.getAvailableDate().isBefore(now) && !studentQuizIds.contains(quiz.getId()))
                .forEach(quiz ->  {
                    QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
                    if (quiz.getConclusionDate() != null && quiz.getConclusionDate().isBefore(now)) {
                        quizAnswer.setCompleted(true);
                    }
                    entityManager.persist(quizAnswer);
                });

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> !quizAnswer.getCompleted() && quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .map(StatementQuizDto::new)
                .sorted(Comparator.comparing(StatementQuizDto::getAvailableDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<SolvedQuizDto> getSolvedQuizzes(String username) {
        User user = userRepository.findByUsername(username);

        return user.getQuizAnswers().stream()
                .filter(QuizAnswer::getCompleted)
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CorrectAnswersDto solveQuiz(String username, @Valid @RequestBody ResultAnswersDto answers) {
        User user = userRepository.findByUsername(username);

        return answerService.submitQuestionsAnswers(user, answers);
    }
}
