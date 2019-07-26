package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.*;

@Component
public class AnswerService {

    @Autowired
    private UserRepository userRepository;

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
    public void createQuizAnswer(Integer userId, Integer quizId, LocalDateTime availableDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, Integer.toString(userId)));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, Integer.toString(quizId)));

        entityManager.persist(new QuizAnswer(user, quiz, availableDate));
    }

    @Transactional
    public CorrectAnswersDto createQuestionAnswer(User user, @Valid @RequestBody ResultAnswersDto answers) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(answers.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, Integer.toString(answers.getQuizAnswerId())));

        quizAnswer.setAnswerDate(answers.getAnswerDate());

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(USER_MISMATCH, user.getUsername());
        }

        for (Map.Entry<Integer, ResultAnswerDto> entrySet : answers.getAnswers().entrySet()) {
            QuizQuestion quizQuestion = quizQuestionRepository.findById(entrySet.getValue().getQuizQuestionId())
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, Integer.toString(entrySet.getValue().getQuizQuestionId())));

            if (isNotAssignedQuestion(quizAnswer, quizQuestion)) {
                throw new TutorException(QUIZ_MISMATCH, Integer.toString(quizQuestion.getQuiz().getId()));
            }
            Option option = optionRepository.findById(entrySet.getValue().getOptionId())
                    .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, Integer.toString(entrySet.getValue().getOptionId())));


            if (isNotQuestionOption(quizQuestion, option)) {
                throw new TutorException(QUIZ_MISMATCH, Integer.toString(option.getId()));
            }

            if (!quizAnswer.getCompleted()) {
                entityManager.persist(new QuestionAnswer(quizAnswer, quizQuestion, entrySet.getValue().getTimeTaken(), option));
            }
        }


        return new CorrectAnswersDto(quizAnswer.getQuiz().getQuizQuestions().stream()
                .collect(Collectors.toMap(QuizQuestion::getSequence, quizQuestion -> new CorrectAnswerDto(quizQuestion))));
    }

    private boolean isNotQuestionOption(QuizQuestion quizQuestion, Option option) {
        return !quizQuestion.getQuestion().getOptions().stream().map(option1 -> option1.getId()).anyMatch(value -> value == option.getId());
    }

    private boolean isNotAssignedQuestion(QuizAnswer quizAnswer, QuizQuestion quizQuestion) {
        return quizQuestion.getQuiz().getId() != quizAnswer.getQuiz().getId();
    }

    private boolean isNotAssignedStudent(User user, QuizAnswer quizAnswer) {
        return user.getId() != quizAnswer.getUser().getId();
    }

}
