package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.FailedAnswerRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class FailedAnswerService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private FailedAnswerRepository failedAnswerRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FailedAnswerDto createFailedAnswer(int dashboardId, int questionAnswerId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));

        FailedAnswer failedAnswer = new FailedAnswer(dashboard, questionAnswer, DateHandler.now());
        failedAnswerRepository.save(failedAnswer);

        return new FailedAnswerDto(failedAnswer);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeFailedAnswer(int failedAnswerId) {
        FailedAnswer toRemove = failedAnswerRepository.findById(failedAnswerId).orElseThrow(() -> new TutorException(ErrorMessage.FAILED_ANSWER_NOT_FOUND, failedAnswerId));
        toRemove.remove();
        failedAnswerRepository.delete(toRemove);
    }


}
