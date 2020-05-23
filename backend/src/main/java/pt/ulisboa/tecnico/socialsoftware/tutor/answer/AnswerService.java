package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDtoFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CorrectAnswerDto> concludeQuiz(User user, Integer quizId) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream().filter(qa -> qa.getQuiz().getId().equals(quizId)).findFirst().orElseThrow(() ->
                new TutorException(QUIZ_NOT_FOUND, quizId));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setAnswerDate(DateHandler.now());
            quizAnswer.setCompleted(true);
        }

        // In class quiz when student submits before resultsDate
        if (quizAnswer.getQuiz().getResultsDate() != null &&
            quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) &&
            DateHandler.now().isBefore(quizAnswer.getQuiz().getResultsDate())) {

            return new ArrayList<>();
        }

        return quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(AnswerDtoFactory::getCorrectAnswerDto)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void submitAnswer(User user, Integer quizId, MultipleChoiceStatementAnswerDto answer) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream()
                .filter(qa -> qa.getQuiz().getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().stream()
                .filter(qa -> qa.getSequence().equals(answer.getSequence()))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answer.getSequence()));

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(QUIZ_USER_MISMATCH, String.valueOf(quizAnswer.getQuiz().getId()), user.getUsername());
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {

            if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
                handleMultipleChoiceQuestionAnswer((MultipleChoiceQuestionAnswer)questionAnswer, answer);
            }
            else{
                // todo we might want to throw an exception if we do not to know how to handle a given type
            }
            questionAnswer.setTimeTaken(answer.getTimeTaken());
            quizAnswer.setAnswerDate(DateHandler.now());
        }
    }

    // todo evaluate if we want to have this being handled by the QuestionAnswer
    private void handleMultipleChoiceQuestionAnswer(MultipleChoiceQuestionAnswer questionAnswer, MultipleChoiceStatementAnswerDto answer){
        if (answer.getOptionId() != null) {
            Option option = optionRepository.findById(answer.getOptionId())
                    .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

            if (isNotQuestionOption(questionAnswer.getQuizQuestion(), option)) {
                throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getQuizQuestion().getQuestion().getId(), option.getId());
            }

            if (questionAnswer.getOption() != null) {
                questionAnswer.getOption().getQuestionAnswers().remove(questionAnswer);
            }

            questionAnswer.setOption(option);
        } else {
            questionAnswer.setOption(null);
        }
    }

    private boolean isNotQuestionOption(QuizQuestion quizQuestion, Option option) {
        return ((MultipleChoiceQuestion)quizQuestion.getQuestion()).getOptions().stream().map(Option::getId).noneMatch(value -> value.equals(option.getId()));
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
        xmlImporter.importAnswers(answersXml);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        List<QuestionAnswer> questionAnswers = new ArrayList<>(quizAnswer.getQuestionAnswers());
        questionAnswers.forEach(questionAnswer ->
        {
            questionAnswer.remove();
            questionAnswerRepository.delete(questionAnswer);
        });
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }
}
