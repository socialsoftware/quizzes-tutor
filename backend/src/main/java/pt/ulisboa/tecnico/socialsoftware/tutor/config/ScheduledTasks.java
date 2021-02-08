package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils;

@Component
public class ScheduledTasks {
	@Autowired
	private ImpExpService impExpService;

	@Autowired
	private DemoUtils demoUtils;

	@Autowired
	private StatementService statementService;

	@Scheduled(cron = "0 0 3,13 * * *")
	public void exportAll() {
		impExpService.exportAll();
	}

	@Scheduled(cron = "0 0 2 * * *")
	public void writeQuizAnswersAndCalculateStatistics() {
		statementService.writeQuizAnswersAndCalculateStatistics();
	}

	@Scheduled(cron = "0 0 1 * * *")
    public void resetDemoInfo() {
		demoUtils.resetDemoInfo();
	}
}