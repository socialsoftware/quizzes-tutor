package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DifficultQuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DifficultQuestionDto createDifficultQuestion(int dashboardId, int questionId, int percentage) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        DifficultQuestion difficultQuestion = new DifficultQuestion(dashboard, question, percentage);
        difficultQuestionRepository.save(difficultQuestion);

        return new DifficultQuestionDto(difficultQuestion);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDifficultQuestion(int difficultQuestionId) {
        DifficultQuestion difficultQuestion = difficultQuestionRepository.findById(difficultQuestionId).orElseThrow(() -> new TutorException(DIFFICULT_QUESTION_NOT_FOUND, difficultQuestionId));

        difficultQuestion.remove();
        difficultQuestionRepository.delete(difficultQuestion);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> getDifficultQuestions(int dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        return dashboard.getDifficultQuestions().stream()
                .filter(Predicate.not(DifficultQuestion::isRemoved))
                .map(DifficultQuestionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateDifficultQuestions(int dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        List<DifficultQuestionDto> lastDifficultQuestionsDtos = new ArrayList<>();
        List<Question> usedQuestions = new ArrayList<>();

        dashboard.getCourseExecution().getQuizzes().stream()
                .flatMap(quiz -> quiz.getQuizQuestions().stream())
                .map(QuizQuestion::getQuestion)
                .findFirst()
                .map(question -> new DifficultQuestion(dashboard, question, 24));

//        for (DifficultQuestion dq : dashboard.getDifficultQuestions()) { //TODO: Change when Dashboard changed
//            if ((dq.getRemovedDate().isAfter(LocalDateTime.now().minusDays(7))) &&
//                    !dq.isRemoved()) {
//                dq.update();
//                if (dq.getPercentage() < 0.25) //TODO: Depends of changes in difficulty
//                {
//                    lastDifficultQuestionsDtos.add(new DifficultQuestionDto(dq));
//                    usedQuestions.add(dq.getQuestion());
//                }
//            } else if (dq.getRemovedDate().isBefore(LocalDateTime.now().minusDays(7))) {
//                dq.remove();
//                difficultQuestionRepository.delete(dq);
//            }
//        }
//
//        List<Quiz> lastWeekQuizzes = dashboard.getCourseExecution().getQuizzes().stream()
//                .filter(q -> q.getConclusionDate().isAfter(LocalDateTime.now().minusDays(7)))
//                .collect(Collectors.toList());
//
//        for (Quiz quiz : lastWeekQuizzes) {
//            for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
//                if (!usedQuestions.contains(quizQuestion.getQuestion())) { //TODO: Might not work due to lack of equals
//                    Question question = quizQuestion.getQuestion();
//
//                    DifficultQuestionDto difficultQuestionDto = createDifficultQuestion(dashboardId, question.getId(), question.getDifficulty());
//
//                    lastDifficultQuestionsDtos.add(difficultQuestionDto);
//                }
//            }
//        }

        ///return lastDifficultQuestionsDtos;
    }
}