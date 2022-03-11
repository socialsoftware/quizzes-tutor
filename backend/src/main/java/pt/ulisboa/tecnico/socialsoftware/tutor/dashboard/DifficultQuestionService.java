package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

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

        List<DifficultQuestionDto> difficultQuestions = dashboard.getDifficultQuestions();

        return difficultQuestions.stream()
                .map(DifficultQuestionDto::new)
                .sorted(Comparator.comparing(DifficultQuestionDto::getCollected, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

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
                dq.remove();
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
}