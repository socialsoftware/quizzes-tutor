package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;

@Component
public class ScheduledTasks {

    @Autowired
    private ImpExpService impExpService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TutorDemoUtils tutorDemoUtils;

    @Scheduled(cron = "0 0 3,13 * * *")
    public void exportAll() {
        impExpService.exportAll();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void writeQuizAnswersAndCalculateStatistics() {
        answerService.writeQuizAnswersAndCalculateStatistics();
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void resetDemoInfo() {
        tutorDemoUtils.resetDemoInfo();
    }

    @Scheduled(cron = "*/300 * * * * *")
    public void populateExternalQuizzes() {
        quizService.populateExternalQuizzesWithQuizAnswers();
    }
}
