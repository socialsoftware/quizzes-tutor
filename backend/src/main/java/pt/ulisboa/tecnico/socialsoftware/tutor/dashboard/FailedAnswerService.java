package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

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
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.FailedAnswerRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

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


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> updateFailedAnswers(int dashboardId) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        int courseExecutionId = dashboard.getCourseExecution().getId();
        int studentId = dashboard.getStudent().getId();

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByStudentAndExecutionCourseId(courseExecutionId, studentId);
        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(QuizAnswer quizAnswer: quizAnswers){
            if(quizAnswer.getCreationDate().isAfter(dashboard.getLastCheckFailedAnswers()) && quizAnswer.isCompleted()){
                for(QuestionAnswer qa: quizAnswer.getQuestionAnswers()){
                    if(!qa.isCorrect()){
                        FailedAnswer fa = new FailedAnswer();
                        fa.setCollected(LocalDateTime.now());
                        fa.setQuestionAnswer(qa);
                        fa.setRemoved(false);
                        fa.setAnswered(qa.isAnswered());
                        fa.setDashboard(dashboard);

                        failedAnswerDtos.add(new FailedAnswerDto(fa));
                        dashboard.addFailedAnswer(fa);
                        failedAnswerRepository.save(fa);
                    }
                }

            }
        }
        return failedAnswerDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> getFailedAnswers(int dashboardId) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        List<FailedAnswer> failedAnswers = dashboard.getFailedAnswers();
        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(FailedAnswer fa: failedAnswers){
            failedAnswerDtos.add(new FailedAnswerDto(fa));
        }

        return failedAnswerDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeFailedAnswer(int failedAnswerId) {
        FailedAnswer toRemove = failedAnswerRepository.findById(failedAnswerId).orElseThrow(() -> new TutorException(ErrorMessage.FAILED_ANSWER_NOT_FOUND, failedAnswerId));
        toRemove.setRemoved(true);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> reAddFailedAnswers(int dashboardId, String startDate, String endDate) {
        checkDates(startDate, endDate);
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        List<FailedAnswer> removedFailedAnswers = dashboard.getAllFailedAnswers().stream().filter(fa -> fa.getCollected().isAfter(LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)) &&
                                                                                    fa.getCollected().isBefore(LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME)) &&
                                                                                    fa.getRemoved()).collect(Collectors.toList());

        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(FailedAnswer failedAnswer: removedFailedAnswers){
            failedAnswer.setRemoved(false);
            failedAnswerDtos.add(new FailedAnswerDto(failedAnswer));
        }

        return failedAnswerDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> getFilteredFailedAnswers(int dashboardId, String startDate, String endDate) {
        checkDates(startDate, endDate);
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        List<FailedAnswer> fas = dashboard.getFailedAnswers().stream().filter(fa -> fa.getCollected().isAfter(LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)) &&
                                                                                    fa.getCollected().isBefore(LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME)) &&
                                                                                    !fa.getRemoved()) .collect(Collectors.toList());

        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(FailedAnswer fa: fas){
            failedAnswerDtos.add(new FailedAnswerDto(fa));
        }

        return failedAnswerDtos;
    }

    private void checkDates(String startDate, String endDate) {
        if (startDate == null) throw new TutorException(FAILED_ANSWER_MISSING_START_TIME);
        if (endDate == null) throw new TutorException(FAILED_ANSWER_MISSING_END_TIME);
        if (LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME).isBefore(LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)))
            throw new TutorException(INVALID_DATE_INTERVAL);
    }
}