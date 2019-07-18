package com.example.tutor.stats;

import com.example.tutor.answer.AnswerRepository;
import com.example.tutor.question.QuestionRepository;
import com.example.tutor.quiz.QuizRepository;
import com.example.tutor.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatsController {

    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;
    private QuizRepository quizRepository;

    StatsController(AnswerRepository answerRepository, QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;

    }

    @GetMapping("/stats")
    public StatsDTO getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        Integer totalQuizzes = this.answerRepository.getTotalQuizzes(user.getId());
        Integer totalUniqueQuestions = this.questionRepository.getTotalUniqueQuestions();

        Integer totalAnswers = this.answerRepository.getTotalAnswers(user.getId());
        Integer uniqueCorrectAnswers = this.answerRepository.getUniqueCorrectAnswers(user.getId());
        Integer uniqueWrongAnswers = this.answerRepository.getUniqueAnswers(user.getId()) - uniqueCorrectAnswers;
        //TopicsStatsDTO[] topics = this.answerRepository.getTopicsStats(user.getId());
        //AnsweredQuizDTO[] quizzes = this.quizRepository.getQuizzes(user.getId());

        return new StatsDTO(totalQuizzes, totalAnswers, uniqueCorrectAnswers, uniqueWrongAnswers, totalUniqueQuestions);
    }
}