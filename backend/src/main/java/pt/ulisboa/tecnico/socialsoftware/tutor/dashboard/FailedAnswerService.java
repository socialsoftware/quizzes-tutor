package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<FailedAnswerDto> updateFailedAnswers(int dashboardId, int courseExecutionId, int studentId) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByStudentAndExecutionCourseId(courseExecutionId, studentId);
        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(QuizAnswer quizAnswer: quizAnswers){
            if(quizAnswer.getCreationDate().isAfter(dashboard.getLastCheckFailedAnswers())){
                for(QuestionAnswer qa: quizAnswer.getQuestionAnswers()){
                    if(!qa.isCorrect()){
                        FailedAnswer fa = new FailedAnswer();
                        fa.setCollected(LocalDateTime.now());
                        fa.setQuestionAnswer(qa);
                        fa.setRemoved(false);
                        fa.setAnswered(qa.isAnswered());

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
    public void removeFailedAnswer(int failedAnswerId, int dashboardId) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        FailedAnswer toRemove = failedAnswerRepository.findById(failedAnswerId).orElseThrow(() -> new TutorException(ErrorMessage.FAILED_ANSWER_NOT_FOUND, failedAnswerId));

        toRemove.setRemoved(true);
        dashboard.removeFailedAnswer(toRemove);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> reAddFailedAnswers(int dashboardId, LocalDateTime startDate, LocalDateTime endDate) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        List<FailedAnswer> fas = dashboard.getFailedAnswers().stream().filter(fa -> fa.getCollected().isAfter(startDate) &&
                                                                                    fa.getCollected().isBefore(endDate) &&
                                                                                    fa.getRemoved() == true) .collect(Collectors.toList());

        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(FailedAnswer fa: fas){

            if(!dashboard.hasFailedAnswer(fa)){
                dashboard.addFailedAnswer(fa);
                fa.setRemoved(false);
                failedAnswerDtos.add(new FailedAnswerDto(fa));
            }
        }

        return failedAnswerDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> getFilteredFailedAnswers(int dashboardId, LocalDateTime startDate, LocalDateTime endDate) {

        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        List<FailedAnswer> fas = dashboard.getFailedAnswers().stream().filter(fa -> fa.getCollected().isAfter(startDate) &&
                                                                                    fa.getCollected().isBefore(endDate) &&
                                                                                    fa.getRemoved() == true) .collect(Collectors.toList());

        List<FailedAnswerDto> failedAnswerDtos = new ArrayList<>();

        for(FailedAnswer fa: fas){
            failedAnswerDtos.add(new FailedAnswerDto(fa));
        }

        return failedAnswerDtos;
    }
}
