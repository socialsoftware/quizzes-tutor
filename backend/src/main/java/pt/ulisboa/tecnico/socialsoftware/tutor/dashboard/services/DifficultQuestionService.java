package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DifficultQuestionService {
    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> updateDifficultQuestions(int dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        LocalDateTime now = DateHandler.now();

        Set<DifficultQuestion> questionsToRemove = dashboard.getDifficultQuestions().stream()
                .filter(difficultQuestion -> !difficultQuestion.isRemoved() ||
                        (difficultQuestion.isRemoved() && difficultQuestion.getRemovedDate().plusDays(7).isBefore((now))))
                .collect(Collectors.toSet());

        questionsToRemove.forEach(difficultQuestion -> {
            difficultQuestion.remove();
            difficultQuestionRepository.delete(difficultQuestion);
        });

        Set<Question> questionsToPersist = dashboard.getDifficultQuestions().stream()
                .map(DifficultQuestion::getQuestion)
                .collect(Collectors.toSet());

        Set<QuizAnswer> answers = quizAnswerRepository.findByCourseExecutionInPeriod(dashboard.getCourseExecution().getId(), now.minusDays(7), now);

        Map<Question, List<QuestionAnswer> > questionsWithAnswers = answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .collect(Collectors.groupingBy(questionAnswer -> questionAnswer.getQuestion()));

        for (Map.Entry<Question, List<QuestionAnswer>> questionWithAnswers: questionsWithAnswers.entrySet()) {
            if (!questionsToPersist.contains(questionWithAnswers.getKey()) && dashboard.getCourseExecution().isQuestionInAvailableAssessment(questionWithAnswers.getKey())) {
                int percentageCorrect = percentageCorrect(questionWithAnswers.getValue());
                if (percentageCorrect < 24) {
                    DifficultQuestion difficultQuestion = new DifficultQuestion(dashboard, questionWithAnswers.getKey(), percentageCorrect);
                    difficultQuestionRepository.save(difficultQuestion);
                }
            }
        }

        dashboard.setLastCheckDifficultQuestions(now);

        return dashboard.getDifficultQuestions().stream()
                .filter(Predicate.not(DifficultQuestion::isRemoved))
                .map(DifficultQuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDifficultQuestion(int difficultQuestionId) {
        DifficultQuestion difficultQuestion = difficultQuestionRepository.findById(difficultQuestionId).orElseThrow(() -> new TutorException(DIFFICULT_QUESTION_NOT_FOUND, difficultQuestionId));

        difficultQuestion.setRemoved(true);
        difficultQuestion.setRemovedDate(DateHandler.now());
    }

    private int percentageCorrect(List<QuestionAnswer> questionAnswers) {
        int totalAnswers = questionAnswers.size();

        if (totalAnswers == 0) {
            return 100;
        }

        int correctAnswers = (int) questionAnswers.stream()
                .filter(QuestionAnswer::isCorrect)
                .count();

        return (correctAnswers * 100) /  totalAnswers;
    }
}