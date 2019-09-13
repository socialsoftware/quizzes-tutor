package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
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
import java.util.Comparator;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.*;

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

    @Transactional
    public QuizAnswer createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, Integer.toString(userId)));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, Integer.toString(quizId)));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        entityManager.persist(quizAnswer);

        return quizAnswer;
    }

    @Transactional
    public void removeQuizAnswer(Integer quizAnswerId) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId)
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerId.toString()));

        quizAnswer.remove();

        entityManager.remove(quizAnswer);
    }

    @Transactional
    public CorrectAnswersDto submitQuestionsAnswers(User user, @Valid @RequestBody ResultAnswersDto answers) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(answers.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, Integer.toString(answers.getQuizAnswerId())));

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(USER_MISMATCH, user.getUsername());
        }

        for (ResultAnswerDto resultAnswerDto : answers.getAnswers()) {
            QuizQuestion quizQuestion = quizQuestionRepository.findById(resultAnswerDto.getQuizQuestionId())
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, Integer.toString(resultAnswerDto.getQuizQuestionId())));

            if (isNotAssignedQuestion(quizAnswer, quizQuestion)) {
                throw new TutorException(QUIZ_MISMATCH, Integer.toString(quizQuestion.getQuiz().getId()));
            }

            Option option = null;

            if (resultAnswerDto.getOptionId() != null) {
                option = optionRepository.findById(resultAnswerDto.getOptionId())
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, Integer.toString(resultAnswerDto.getOptionId())));


                if (isNotQuestionOption(quizQuestion, option)) {
                    throw new TutorException(QUIZ_MISMATCH, Integer.toString(option.getId()));
                }
            }

            entityManager.persist(new QuestionAnswer(quizAnswer, quizQuestion, resultAnswerDto.getTimeTaken(), option));
        }

        quizAnswer.setAnswerDate(answers.getAnswerDate());
        quizAnswer.setCompleted(true);

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

    @Transactional
    public String exportAnswers() {
        AnswersXmlExport xmlExport = new AnswersXmlExport();

        return xmlExport.export(quizAnswerRepository.findAll());
    }

    @Transactional
    public void importAnswers(String answersXml) {
        AnswersXmlImport xmlImporter = new AnswersXmlImport();

        xmlImporter.importAnswers(answersXml, this, questionRepository, quizRepository, userRepository);
    }

}
