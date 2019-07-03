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
import java.util.Arrays;
import java.util.List;
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

        // Get Quiz
        Quiz quiz = quizRepository.findById(answers.getQuizId()).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + answers.getQuizId()));

        // Verify that the list of questions received is the same as the questions associated with the quiz
        if (! Arrays.equals(quiz.getQuestions().stream().map(Question::getId).sorted().toArray(), answers.getAnswers().stream().map(ResultAnswerDTO::getQuestionId).sorted().toArray())) {
            throw new WrongParametersException("Quiz " + quiz.getId() + " question's ids do not match");
        }

        List<CorrectAnswerDTO> correctAnswers = answers.getAnswers().stream().map(answer -> {
            Question q = quiz.getQuestions().stream().filter(question -> question.getId().equals(answer.getQuestionId())).findFirst().get();
            answerRepository.save(new Answer(answer, user, q, answers.getAnswerDate(), answers.getQuizId()));
            return new CorrectAnswerDTO(q);
        }).collect(Collectors.toList());

        return new CorrectAnswersDTO(correctAnswers);

    }
}