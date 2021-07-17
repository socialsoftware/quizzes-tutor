package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;

@Component
public class ScheduledTasks {
	@Autowired
	private ImpExpService impExpService;

	@Autowired
	private TutorDemoUtils tutorDemoUtils;

	@Autowired
	private AnswerService answerService;

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
}