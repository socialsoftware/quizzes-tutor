package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

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

    public CorrectAnswersDto submitQuestionsAnswers(User user, @Valid @RequestBody ResultAnswersDto answers) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(answers.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, answers.getQuizAnswerId()));

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(QUIZ_USER_MISMATCH, String.valueOf(quizAnswer.getId()), user.getUsername());
        }
        if (!quizAnswer.getCompleted()){
            int correctAnswers = 0;

            for(int sequence = 0; sequence < answers.getAnswers().size(); sequence++) {
                ResultAnswerDto resultAnswerDto = answers.getAnswers().get(sequence);
                QuizQuestion quizQuestion = quizQuestionRepository.findById(resultAnswerDto.getQuizQuestionId())
                        .orElseThrow(() -> new TutorException(QUIZ_QUESTION_NOT_FOUND, resultAnswerDto.getQuizQuestionId()));

                if (isNotAssignedQuestion(quizAnswer, quizQuestion)) {
                    throw new TutorException(QUIZ_MISMATCH, quizAnswer.getId(), quizQuestion.getQuiz().getId());
                }

                Option option = null;
                if (resultAnswerDto.getOptionId() != null) {
                    option = optionRepository.findById(resultAnswerDto.getOptionId())
                            .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, resultAnswerDto.getOptionId()));

                    if (option.getCorrect()) {
                        correctAnswers += 1;
                    }


                    if (isNotQuestionOption(quizQuestion, option)) {
                        throw new TutorException(QUIZ_OPTION_MISMATCH, quizQuestion.getId(), option.getId());
                    }
                }
                entityManager.persist(new QuestionAnswer(quizAnswer, quizQuestion, resultAnswerDto.getTimeTaken(), option, sequence));

            }
            quizAnswer.setAnswerDate(answers.getAnswerDate());
            quizAnswer.setCompleted(true);

            // Increase stats to be shown in user list
            if (quizAnswer.getQuiz().getType() == Quiz.QuizType.TEACHER) {
                user.increaseNumberOfTeacherQuizzes();
                user.increaseNumberOfTeacherAnswers(answers.getAnswers().size());
                user.increaseNumberOfCorrectTeacherAnswers(correctAnswers);

            } else {
               user.increaseNumberOfStudentQuizzes();
            }
            user.increaseNumberOfAnswers(answers.getAnswers().size());
            user.increaseNumberOfCorrectAnswers(correctAnswers);
        }

        return new CorrectAnswersDto(quizAnswer.getQuiz().getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(CorrectAnswerDto::new).collect(Collectors.toList()));
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
