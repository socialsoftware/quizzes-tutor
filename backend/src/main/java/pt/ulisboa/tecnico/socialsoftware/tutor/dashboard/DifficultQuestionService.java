package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_DELETE_SUBMITTED_QUESTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;

public class DifficultQuestionService {

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;


    @Autowired
    private DashboardRepository dashboardRepository;


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> updateDifficultQuestions(int dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        List <DifficultQuestionDto> lastDifficultQuestionsDtos = new ArrayList();
        List <Question> usedQuestions = new ArrayList<>();

        for(DifficultQuestion dq: dashboard.getDifficultQuestions()){ //TODO: Change when Dashboard changed
            if ((dq.getCollected().isAfter(LocalDateTime.now().minusDays(7))) &&
                    !dq.isRemoved()){
                dq.update();
                if(dq.getPercentage() < 0.25) //TODO: Depends of changes in difficulty
                {
                    lastDifficultQuestionsDtos.add(new DifficultQuestionDto(dq));
                    usedQuestions.add(dq.getQuestion());
                }
            }
            else if(dq.getCollected().isBefore(LocalDateTime.now().minusDays(7))){
                difficultQuestionRepository.delete(dq);
            }
        }

        List<Quiz> lastWeekQuizzes = dashboard.getCourseExecution().getQuizzes(). stream()
                .filter(q -> q.getConclusionDate().isAfter(LocalDateTime.now().minusDays(7)))
                .collect(Collectors.toList());

        for(Quiz quiz: lastWeekQuizzes){
            for(QuizQuestion q: quiz.getQuizQuestions()){
                if(!usedQuestions.contains(q.getQuestion())){ //TODO: Might not work due to lack of equals
                    DifficultQuestion dq = new DifficultQuestion();
                    dq.setRemoved(false);
                    dq.setQuestion(q.getQuestion());
                    dq.setCollected(LocalDateTime.now());
                    dq.setDashboard(dashboard);
                    dq.update();

                    lastDifficultQuestionsDtos.add(new DifficultQuestionDto(dq));
                }
            }
        }

        return lastDifficultQuestionsDtos;
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
        difficultQuestionRepository.findAll()
                .stream()
                .forEach(difficultQuestion -> {
                    difficultQuestion.setRemoved(false);
                });
    }
}