package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AssessmentService {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicConjunctionRepository topicConjunctionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseService courseService;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> findAssessments(int courseExecutionId) {
        return assessmentRepository.findByExecutionCourseId(courseExecutionId).stream()
                .map(AssessmentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> findAvailableAssessments(int courseExecutionId) {
        return assessmentRepository.findByExecutionCourseId(courseExecutionId).stream()
                .filter(assessment -> assessment.getStatus() == Assessment.Status.AVAILABLE)
                .map(AssessmentDto::new)
                .sorted(Comparator.comparing(AssessmentDto::getSequence, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(int executionId, AssessmentDto assessmentDto) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<TopicConjunction> topicConjunctions = assessmentDto.getTopicConjunctions().stream()
                .map(topicConjunctionDto -> {
                    TopicConjunction topicConjunction = new TopicConjunction();
                    Set<Topic> newTopics = topicConjunctionDto.getTopics().stream().map(topicDto -> topicRepository.findById(topicDto.getId()).orElseThrow()).collect(Collectors.toSet());
                    topicConjunction.updateTopics(newTopics);
                    return topicConjunction;
                }).collect(Collectors.toList());

        Assessment assessment = new Assessment(courseExecution, topicConjunctions, assessmentDto);
        assessmentRepository.save(assessment);

        return new AssessmentDto(assessment);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto updateAssessment(Integer assessmentId, AssessmentDto assessmentDto) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));

        assessment.setTitle(assessmentDto.getTitle());
        assessment.setStatus(Assessment.Status.valueOf(assessmentDto.getStatus()));
        assessment.setSequence(assessmentDto.getSequence());

        // remove TopicConjunction that are not in the Dto
        assessment.getTopicConjunctions().stream()
                .filter(topicConjunction -> assessmentDto.getTopicConjunctions().stream().noneMatch(topicConjunctionDto -> topicConjunction.getId().equals(topicConjunctionDto.getId())))
                .collect(Collectors.toList())
                .forEach(topicConjunction -> {
                    topicConjunction.remove();
                    topicConjunctionRepository.delete(topicConjunction);
                });

        for (TopicConjunctionDto topicConjunctionDto: assessmentDto.getTopicConjunctions()) {
            // topicConjunction already existed
            if (topicConjunctionDto.getId() != null) {
                TopicConjunction topicConjunction = topicConjunctionRepository.findById(topicConjunctionDto.getId()).orElseThrow(() -> new TutorException(TOPIC_CONJUNCTION_NOT_FOUND, topicConjunctionDto.getId()));
                Set<Topic> newTopics = topicConjunctionDto.getTopics().stream()
                        .map(topicDto -> topicRepository.findById(topicDto.getId()).orElseThrow()).collect(Collectors.toSet());
                topicConjunction.updateTopics(newTopics);
            } else {
                // new topicConjunction
                TopicConjunction topicConjunction = new TopicConjunction();
                Set<Topic> newTopics = topicConjunctionDto.getTopics().stream()
                        .map(topicDto -> topicRepository.findById(topicDto.getId()).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId())))
                        .collect(Collectors.toSet());
                topicConjunction.updateTopics(newTopics);
                topicConjunction.setAssessment(assessment);
                topicConjunctionRepository.save(topicConjunction);
            }
        }

        return new AssessmentDto(assessment);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeAssessment(Integer assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));
        assessment.remove();
        assessmentRepository.delete(assessment);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void assessmentSetStatus(Integer assessmentId, Assessment.Status status) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));
        assessment.setStatus(status);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAssessments() {
        this.assessmentRepository.findByExecutionCourseId(courseService.getDemoCourse().getCourseExecutionId())
                .stream()
                .skip(5)
                .forEach(assessment -> assessmentRepository.delete(assessment));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> getAssessmentQuestions(Integer assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));

        return assessment.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }
}

