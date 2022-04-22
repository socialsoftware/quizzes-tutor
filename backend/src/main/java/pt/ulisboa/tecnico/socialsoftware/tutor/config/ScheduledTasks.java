package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.DifficultQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils;

import java.util.List;

@Component
public class ScheduledTasks {
	@Autowired
	private ImpExpService impExpService;

	@Autowired
	private DemoUtils demoUtils;

	@Autowired
	private AnswerService answerService;

	@Autowired
	private CourseExecutionRepository courseExecutionRepository;

	@Autowired
	private DifficultQuestionService difficultQuestionService;

	@Scheduled(cron = "0 0 3,13 * * *")
	public void exportAll() {
		impExpService.exportAll();
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void resetDemoInfo() {
		demoUtils.resetDemoInfo();
	}

	@Scheduled(cron = "0 0 2 * * *")
	public void writeQuizAnswersAndCalculateStatistics() {
		answerService.writeQuizAnswersAndQuestionStatistics();
	}

	@Scheduled(cron = "0 0 5 * * *")
	public void updateCourseExecutionsDifficultQuestions() {
		List<CourseExecution> courseExecutions = courseExecutionRepository.findAll();

		courseExecutions.forEach(courseExecution -> difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(courseExecution.getId()));
	}


}