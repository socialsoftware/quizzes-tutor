package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_STUDENT" })
public class StatsController {

    @GetMapping("/stats")
    public StatsDto getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        StatsDto statsDto = new StatsDto();

        Integer totalAnswers = user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .map(Set::size)
                .reduce(0, Integer::sum);

        Integer uniqueQuestions = user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .collect(Collectors.toSet())
                .size();

        Integer correctAnswers = (int) user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Option::getCorrect).count();

        System.out.println(user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Option::getCorrect).count());

        // TODO this requires atention
        Integer uniqueCorrectAnswers = user.getQuizAnswers().stream()
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Option::getCorrect)
                .map(Option::getQuestion)
                .map(Question::getId)
                .collect(Collectors.toSet())
                .size();

        statsDto.setTotalQuizzes((int) user.getQuizAnswers().stream().filter(QuizAnswer::getCompleted).count());
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float)correctAnswers)*100/totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }
        return statsDto;
    }
}