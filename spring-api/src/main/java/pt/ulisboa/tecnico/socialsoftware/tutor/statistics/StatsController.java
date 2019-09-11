package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatsController {

    private QuestionAnswerRepository questionAnswerRepository;
    private QuestionRepository questionRepository;
    private QuizRepository quizRepository;

    StatsController(QuestionAnswerRepository questionAnswerRepository, QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionAnswerRepository = questionAnswerRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;

    }

    @GetMapping("/stats")
    public StatsDto getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        Integer totalQuizzes = this.questionAnswerRepository.getTotalQuizzes(user.getId());
        Integer totalActiveQuestions = this.questionRepository.getTotalActiveQuestions();

        Integer totalAnswers = this.questionAnswerRepository.getTotalAnswers(user.getId());
        //Integer uniqueCorrectAnswers = this.questionAnswerRepository.getUniqueCorrectAnswers(user.getId());
        Integer uniqueWrongAnswers = this.questionAnswerRepository.getUniqueAnswers(user.getId()) - 0;
        //Integer uniqueWrongAnswers = this.questionAnswerRepository.getUniqueAnswers(user.getId()) - uniqueCorrectAnswers;
        //TopicsStatsDto[] topics = this.answerRepository.getTopicsStats(user.getId());
        //AnsweredQuizDto[] quizzes = this.quizRepository.getQuizzes(user.getId());

        //return new StatsDto(totalQuizzes, totalAnswers, uniqueCorrectAnswers, uniqueWrongAnswers, totalActiveQuestions);
        return new StatsDto(totalQuizzes, totalAnswers, 0, uniqueWrongAnswers, totalActiveQuestions);
    }
}