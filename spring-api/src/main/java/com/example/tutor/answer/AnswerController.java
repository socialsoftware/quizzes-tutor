package com.example.tutor.answer;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.exceptions.WrongParametersException;
import com.example.tutor.question.Question;
import com.example.tutor.quiz.Quiz;
import com.example.tutor.quiz.QuizRepository;
import com.example.tutor.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AnswerController {

    private AnswerRepository answerRepository;
    private QuizRepository quizRepository;

    AnswerController(AnswerRepository answerRepository, QuizRepository quizRepository) {
        this.answerRepository = answerRepository;
        this.quizRepository = quizRepository;
    }

    @PostMapping("/quiz-answers")
    public CorrectAnswersDTO correctAnswers(Principal principal, @Valid @RequestBody ResultAnswersDTO answers) {

        User user = (User) ((Authentication) principal).getPrincipal();

        Quiz quiz = quizRepository.findById(answers.getQuizId()).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + answers.getQuizId()));

        // Verify this quiz was generated for this student (maybe its overkill verification)
        if (! quiz.getGeneratedBy().equals(user.getId())) {
            throw new WrongParametersException("Quiz " + quiz.getId() + " was not generated to " + user.getUsername());
        }

        // For each received student answer, get correct answer
        Map<Integer, CorrectAnswerDTO> correctAnswers = answers.getAnswers().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            Integer sequence = entry.getKey();
            Question question = quiz.getQuestions().get(sequence);
            ResultAnswerDTO answer = answers.getAnswers().get(sequence);

            System.out.println(":" + sequence + ":" + question + ":" + answer);

            // Verify that question ids match (maybe its overkill verification)
            if (! question.getId().equals(answer.getQuestionId())) {
                throw new WrongParametersException("Answer in position" + sequence + " does not have " + user.getUsername());
            }

            if (! quiz.getCompleted()) {
                answerRepository.save(new Answer(answer, user, question, answers.getAnswerDate(), answers.getQuizId()));
            }

            return new CorrectAnswerDTO(question);
        }));



        quiz.setCompleted(true);
        quizRepository.save(quiz);

        return new CorrectAnswersDTO(correctAnswers);

    }
}