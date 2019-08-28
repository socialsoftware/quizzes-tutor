package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service.ImpExpService;

import java.io.IOException;

@Component
public class ScheduledTasks {
	private static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	ImpExpService service;

	@Scheduled(cron = "0 0 1,13 * * *")
	public void exportAll() throws IOException {
		service.exportAll();
	}

}