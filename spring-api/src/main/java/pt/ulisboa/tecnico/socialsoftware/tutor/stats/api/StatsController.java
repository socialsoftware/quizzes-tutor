package pt.ulisboa.tecnico.socialsoftware.tutor.stats.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.stats.dto.StatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
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
    public StatsDto getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        Integer totalQuizzes = this.answerRepository.getTotalQuizzes(user.getId());
        Integer totalActiveQuestions = this.questionRepository.getTotalActiveQuestions();

        Integer totalAnswers = this.answerRepository.getTotalAnswers(user.getId());
        Integer uniqueCorrectAnswers = this.answerRepository.getUniqueCorrectAnswers(user.getId());
        Integer uniqueWrongAnswers = this.answerRepository.getUniqueAnswers(user.getId()) - uniqueCorrectAnswers;
        //TopicsStatsDto[] topics = this.answerRepository.getTopicsStats(user.getId());
        //AnsweredQuizDto[] quizzes = this.quizRepository.getQuizzes(user.getId());

        return new StatsDto(totalQuizzes, totalAnswers, uniqueCorrectAnswers, uniqueWrongAnswers, totalActiveQuestions);
    }
}