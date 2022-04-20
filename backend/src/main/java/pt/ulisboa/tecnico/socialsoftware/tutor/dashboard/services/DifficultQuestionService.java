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
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.RemovedDifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DifficultQuestionService {
    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateCourseExecutionWeekDifficultQuestions(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Set<DifficultQuestion> questionsToRemove = new HashSet<>(courseExecution.getDifficultQuestions());
        questionsToRemove.forEach(difficultQuestion -> {
            difficultQuestion.remove();
            difficultQuestionRepository.delete(difficultQuestion);
        });

        LocalDateTime now = DateHandler.now();

        Set<QuizAnswer> answers = quizAnswerRepository.findByCourseExecutionInPeriod(courseExecution.getId(), now.minusDays(7), DateHandler.now());

        Map<Question, List<QuestionAnswer>> questionsWithAnswers = answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .collect(Collectors.groupingBy(questionAnswer -> questionAnswer.getQuestion()));

        for (Map.Entry<Question, List<QuestionAnswer>> questionWithAnswers : questionsWithAnswers.entrySet()) {
            if (courseExecution.isQuestionInAvailableAssessment(questionWithAnswers.getKey())) {
                int percentageCorrect = percentageCorrect(questionWithAnswers.getValue());
                if (percentageCorrect < 24) {
                    DifficultQuestion difficultQuestion = new DifficultQuestion(courseExecution, questionWithAnswers.getKey(), percentageCorrect);
                    difficultQuestionRepository.save(difficultQuestion);
                }
            }
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DifficultQuestionDto> updateDashboardDifficultQuestions(int dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        LocalDateTime now = DateHandler.now();

        Set<RemovedDifficultQuestion> questionsToRemove = dashboard.getRemovedDifficultQuestions().stream()
                .filter(difficultQuestion -> difficultQuestion.getRemovedDate().plusDays(7).isBefore((now)))
                .collect(Collectors.toSet());

        questionsToRemove.forEach(removedDifficultQuestion -> {
            dashboard.removeRemovedDifficultQuestion(removedDifficultQuestion);
        });

        Set<Integer> removedQuestionsIds = dashboard.getRemovedDifficultQuestions().stream()
                .map(RemovedDifficultQuestion::getQuestionId)
                .collect(Collectors.toSet());

        return dashboard.getCourseExecution().getDifficultQuestions().stream()
                .filter(difficultQuestion -> !removedQuestionsIds.contains(difficultQuestion.getQuestion().getId()))
                .map(DifficultQuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDifficultQuestion(int studentId, int difficultQuestionId) {
        DifficultQuestion difficultQuestion = difficultQuestionRepository.findById(difficultQuestionId).orElseThrow(() -> new TutorException(DIFFICULT_QUESTION_NOT_FOUND, difficultQuestionId));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        RemovedDifficultQuestion removedDifficultQuestion = new RemovedDifficultQuestion(difficultQuestion.getQuestion().getId(), DateHandler.now());

        student.getCourseExecutionDashboard(difficultQuestion.getCourseExecution()).addRemovedDifficultQuestion(removedDifficultQuestion);
    }

    private int percentageCorrect(List<QuestionAnswer> questionAnswers) {
        int totalAnswers = questionAnswers.size();

        if (totalAnswers == 0) {
            return 100;
        }

        int correctAnswers = (int) questionAnswers.stream()
                .filter(QuestionAnswer::isCorrect)
                .count();

        return (correctAnswers * 100) / totalAnswers;
    }
}