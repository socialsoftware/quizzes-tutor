package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Answer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public CorrectAnswersDto correctAnswers(User user, @Valid @RequestBody ResultAnswersDto answers) {

        Quiz quiz = quizRepository.findById(answers.getQuizId()).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + answers.getQuizId()));

        // TODO: when the new code is defined
        // Verify this quiz was generated for this student (maybe its overkill verification)
//        if (! quiz.getGeneratedBy().equals(user.getId())) {
//            throw new WrongParametersException("Quiz " + quiz.getId() + " was not generated to " + user.getUsername());
//        }

        // For each received student answer, get correct answer
        Map<Integer, CorrectAnswerDto> correctAnswers = answers.getAnswers().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Integer sequence = entry.getKey();
                    QuizQuestion question = quiz.getQuizQuestions().get(sequence);
                    ResultAnswerDto answer = answers.getAnswers().get(sequence);

                    System.out.println(":" + sequence + ":" + question + ":" + answer);

                    // TODO: to be checked later

                    // Verify that question ids match (maybe its overkill verification)
//            if (! question.getId().equals(answer.getQuestionId())) {
//                throw new WrongParametersException("Answer in position" + sequence + " does not have " + user.getUsername());
//            }

                    // TODO: needs to be redone in the new context

                    //    if (! quiz.getCompleted()) {
                    answerRepository.save(new Answer(answer, question, user, answers.getAnswerDate()));
                    //   }

                    return new CorrectAnswerDto(question.getQuestion());
                }));



        //  quiz.setCompleted(true);
        quizRepository.save(quiz);

        return null;
        // return new CorrectAnswersDto(correctAnswers);

    }
}
