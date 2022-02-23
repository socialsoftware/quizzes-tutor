package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;

import java.sql.SQLException;
import java.util.List;

public class DifficultQuestionService {

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> updateFailedAnswers(int courseExecutionId, int userId) {
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> getFailedAnswers(int courseExecutionId, int userId) {
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DifficultQuestionDto removeFailedAnswer(int failedAnswerId) {
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> reAddFailedAnswers(int courseExecutionId, int userId, String startDate, String endDate) {
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> getFilteredFailedAnswers(int courseExecutionId, int userId, String startDate, String endDate) {
        return null;
    }
}