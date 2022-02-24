package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_DELETE_SUBMITTED_QUESTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;

public class DifficultQuestionService {

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> updateDifficultQuestions(int dashboardId) {
        // TODO:
        // find all difficult questions
        // update all difficult questions
        // remove if percentage < 75
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> getDifficultQuestions(int dashboardId) {
        // TODO:
        // Get all Difficult Questions
        return null;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DifficultQuestionDto removeDifficultQuestion(int difficultQuestionId) {
        DifficultQuestion difficultQuestion = difficultQuestionRepository.findById(difficultQuestionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, difficultQuestionId));

        difficultQuestion.setCollected(LocalDateTime.now());
        difficultQuestion.setRemoved(true);

        return new DifficultQuestionDto(difficultQuestion);
    }

    @Scheduled(cron = "0 1 ? * SUN")
    public void weeklyRemoveReset(int dashboardId) {
       //TODO:
        // Get all DifficultQuestions
        // Place all Removed False
    }
}