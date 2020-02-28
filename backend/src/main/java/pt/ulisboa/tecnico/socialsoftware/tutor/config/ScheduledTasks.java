package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;

import java.io.IOException;

@Component
public class ScheduledTasks {
	@Autowired
	private ImpExpService impExpService;

	@Autowired
	private StatementService statementService;

	@Scheduled(cron = "0 0 1,13 * * *")
	public void exportAll() throws IOException {
		impExpService.exportAll();
	}

	@Scheduled(cron = "0 0/15 * * * *")
	public void completeOpenQuizAnswers() {
		statementService.completeOpenQuizAnswers();
	}


}