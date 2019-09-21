package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public StatsDto getStats(String username) {
        User user = userRepository.findByUsername(username);

        StatsDto statsDto = new StatsDto();

        int totalAnswers = user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .map(Set::size)
                .reduce(0, Integer::sum);

        int uniqueQuestions = user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .collect(Collectors.toSet())
                .size();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = user.getQuizAnswers().stream()
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
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
