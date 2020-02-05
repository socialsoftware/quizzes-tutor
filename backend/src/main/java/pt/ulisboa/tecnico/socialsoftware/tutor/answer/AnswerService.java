package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private OptionRepository optionRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        entityManager.persist(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeQuizAnswer(Integer quizAnswerId) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId)
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerId));

        quizAnswer.remove();

        entityManager.remove(quizAnswer);
    }

    public CorrectAnswersDto concludeQuiz(User user, Integer quizId) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream().filter(qa -> qa.getQuiz().getId().equals(quizId)).findFirst().orElseThrow(() ->
                new TutorException(QUIZ_NOT_FOUND, quizId));

        quizAnswer.setAnswerDate(LocalDateTime.now());
        quizAnswer.setCompleted(true);

        return new CorrectAnswersDto(quizAnswer.getQuiz().getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(CorrectAnswerDto::new).collect(Collectors.toList()));
    }

    public void submitAnswer(User user, Integer quizId, StatementAnswerDto answer) {

        QuestionAnswer questionsAnswer = questionAnswerRepository.findById(answer.getId()).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answer.getId()));

        if (isNotAssignedStudent(user, questionsAnswer.getQuizAnswer())) {
            throw new TutorException(QUIZ_USER_MISMATCH, String.valueOf(questionsAnswer.getQuizAnswer().getId()), user.getUsername());
        }

        if(questionsAnswer.getQuizQuestion().getQuiz().getConclusionDate() != null && questionsAnswer.getQuizQuestion().getQuiz().getConclusionDate().isBefore(LocalDateTime.now())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if(questionsAnswer.getQuizQuestion().getQuiz().getAvailableDate() != null && questionsAnswer.getQuizQuestion().getQuiz().getAvailableDate().isAfter(LocalDateTime.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if(!questionsAnswer.getQuizAnswer().getCompleted()) {

            Option option = null;
            if (answer.getOptionId() != null) {
                option = optionRepository.findById(answer.getOptionId())
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

                if (isNotQuestionOption(questionsAnswer.getQuizQuestion(), option)) {
                    throw new TutorException(QUIZ_OPTION_MISMATCH, questionsAnswer.getQuizQuestion().getId(), option.getId());
                }
            }
            questionsAnswer.setOption(option);
            questionsAnswer.setTimeTaken(answer.getTimeTaken());
            questionsAnswer.setSequence(answer.getSequence());
            questionsAnswer.getQuizAnswer().setAnswerDate(answer.getAnswerDate());


//            // Increase stats to be shown in user list
//            if (quizAnswer.getQuiz().getType() == Quiz.QuizType.TEACHER) {
//                user.increaseNumberOfTeacherQuizzes();
//                user.increaseNumberOfTeacherAnswers(1);
//                if (option.getCorrect()) {
//                user.increaseNumberOfCorrectTeacherAnswers(correctAnswers);
//                }
//
//            } else {
//                user.increaseNumberOfStudentQuizzes();
//            }
//            user.increaseNumberOfAnswers(answers.getAnswers().size());
//            user.increaseNumberOfCorrectAnswers(correctAnswers);
        }
    }

    private boolean isNotQuestionOption(QuizQuestion quizQuestion, Option option) {
        return quizQuestion.getQuestion().getOptions().stream().map(Option::getId).noneMatch(value -> value.equals(option.getId()));
    }

    private boolean isNotAssignedQuestion(QuizAnswer quizAnswer, QuizQuestion quizQuestion) {
        return !quizQuestion.getQuiz().getId().equals(quizAnswer.getQuiz().getId());
    }

    private boolean isNotAssignedStudent(User user, QuizAnswer quizAnswer) {
        return !user.getId().equals(quizAnswer.getUser().getId());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportAnswers() {
        AnswersXmlExport xmlExport = new AnswersXmlExport();

        return xmlExport.export(quizAnswerRepository.findAll());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importAnswers(String answersXml) {
        AnswersXmlImport xmlImporter = new AnswersXmlImport();

        xmlImporter.importAnswers(answersXml, this, questionRepository, quizRepository, quizAnswerRepository, userRepository);
    }

}
