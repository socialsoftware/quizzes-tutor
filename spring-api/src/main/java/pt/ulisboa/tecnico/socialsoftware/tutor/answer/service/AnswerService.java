package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorNotFoundException;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorNotFoundException(USER_NOT_FOUND, Integer.toString(userId)));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorNotFoundException(QUIZ_NOT_FOUND, Integer.toString(quizId)));

        entityManager.persist(new QuizAnswer(user, quiz, availableDate));
    }

    @Transactional
    public CorrectAnswersDto correctAnswers(User user, @Valid @RequestBody ResultAnswersDto answers) {

        QuizAnswer quizAnswer = quizAnswerRepository.findById(answers.getQuizAnswerId())
                .orElseThrow(() -> new TutorNotFoundException(QUIZ_ANSWER_NOT_FOUND, Integer.toString(answers.getQuizAnswerId())));

        quizAnswer.setAnswerDate(answers.getAnswerDate());

        if (user.getId() != quizAnswer.getUser().getId()) {
            throw new TutorNotFoundException(USER_MISMATCH, user.getUsername());
        }

        for (Map.Entry<Integer, ResultAnswerDto> entrySet : answers.getAnswers().entrySet()) {
            QuizQuestion quizQuestion = quizQuestionRepository.findById(entrySet.getValue().getQuizQuestionId())
                     .orElseThrow(() -> new TutorNotFoundException(QUIZ_QUESTION_NOT_FOUND, Integer.toString(entrySet.getValue().getQuizQuestionId())));

            if (quizQuestion.getQuiz().getId() != quizAnswer.getQuiz().getId()) {
                throw new TutorNotFoundException(QUIZ_MISMATCH, Integer.toString(quizQuestion.getQuiz().getId()));
            }

            Option option = optionRepository.findById(entrySet.getValue().getOptionId())
                    .orElseThrow(() -> new TutorNotFoundException(OPTION_NOT_FOUND, Integer.toString(entrySet.getValue().getOptionId())));


            if (!quizQuestion.getQuestion().getOptions().stream().map(option1 -> option1.getId()).anyMatch(value -> value == option.getId())) {
                throw new TutorNotFoundException(QUIZ_MISMATCH, Integer.toString(option.getId()));
            }

            entityManager.persist(new QuestionAnswer(quizAnswer, quizQuestion, entrySet.getValue().getTimeTaken(), option));
        }


//        Map<Integer, CorrectAnswerDto> correctAnswers = answers.getAnswers().entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
//                    Integer sequence = entry.getKey();
//                    QuizQuestion question = quiz.getQuizQuestionsMap().get(sequence);
//                    ResultAnswerDto answer = answers.getAnswers().get(sequence);
//
//                    System.out.println(":" + sequence + ":" + question + ":" + answer);
//
//                    // TODO: to be checked later
//
//                    // Verify that question ids match (maybe its overkill verification)
////            if (! question.getId().equals(answer.getQuizQuestionId())) {
////                throw new WrongParametersException("QuestionAnswer in position" + sequence + " does not have " + user.getUsername());
////            }
//
//                    // TODO: needs to be redone in the new context
//
//                    //    if (! quiz.getCompleted()) {
//                    questionAnswerRepository.save(new QuestionAnswer(answer, question, user, answers.getAnswerDate()));
//                    //   }
//
//                    return new CorrectAnswerDto(question.getQuestion());
//                }));



        //  quiz.setCompleted(true);
       // quizRepository.save(quiz);

        return null;
        // return new CorrectAnswersDto(correctAnswers);
    }

}
