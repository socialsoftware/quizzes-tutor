package pt.ulisboa.tecnico.socialsoftware.tutor.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.TopicConjunctionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private QuestionRepository questionRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> findAssessments(int courseExecutionId) {
        return assessmentRepository.findByExecutionCourseId(courseExecutionId).stream()
                .map(AssessmentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicConjunctionDto> findTopicConjunctions(int executionId, int assessmentId) {
        List<TopicConjunctionDto> topicConjunctions;
        topicConjunctions = topicConjunctionRepository.findAssessmentTopicConjunctions(assessmentId).stream()
                .map(TopicConjunctionDto::new)
                .collect(Collectors.toList());

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

        List<Question> questions = questionRepository.findQuestions(courseExecution.getCourse().getId());

        Set<String> topicConjunctionTopics = new HashSet<>();
        topicConjunctions.forEach(topicConjunctionDto -> {
            String topics = topicConjunctionDto.getTopics().stream().map(TopicDto::getName).sorted().collect(Collectors.joining());
            topicConjunctionTopics.add(topics);
        });

        questions.forEach(question -> {
            String topics = question.getTopics().stream().map(Topic::getName).sorted().collect(Collectors.joining());
            if (!topicConjunctionTopics.contains(topics)) {
                TopicConjunctionDto topicConjunction = new TopicConjunctionDto(question.getTopics());

                topicConjunctions.add(topicConjunction);
                topicConjunctionTopics.add(topics);
            }
        });

        return topicConjunctions;
    }

    @Retryable(
            value = {SQLException.class},
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
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(int executionId, AssessmentDto assessmentDto) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<TopicConjunction> topicConjunctions = createTopicConjunctions(assessmentDto);

        Assessment assessment = new Assessment(courseExecution, topicConjunctions, assessmentDto);
        assessmentRepository.save(assessment);

        return new AssessmentDto(assessment);
    }

    private List<TopicConjunction> createTopicConjunctions(AssessmentDto assessmentDto) {
        return assessmentDto.getTopicConjunctions().stream()
                .map(topicConjunctionDto -> {
                    TopicConjunction topicConjunction = new TopicConjunction();
                    Set<Topic> newTopics = topicConjunctionDto.getTopics().stream()
                            .map(topicDto -> topicRepository.findById(topicDto.getId())
                                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId())))
                            .collect(Collectors.toSet());
                    topicConjunction.updateTopics(newTopics);
                    return topicConjunction;
                }).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto updateAssessment(Integer assessmentId, AssessmentDto assessmentDto) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));

        assessment.setTitle(assessmentDto.getTitle());
        assessment.setStatus(Assessment.Status.valueOf(assessmentDto.getStatus()));
        assessment.setSequence(assessmentDto.getSequence());

        Set<TopicConjunction> topicConjunctionsToDelete = assessment.getTopicConjunctions().stream()
                .collect(Collectors.toSet());
        topicConjunctionsToDelete.forEach(topicConjunction -> {
            topicConjunction.remove();
            topicConjunctionRepository.delete(topicConjunction);

        });

        for (TopicConjunctionDto topicConjunctionDto : assessmentDto.getTopicConjunctions()) {
            TopicConjunction topicConjunction = new TopicConjunction();
            Set<Topic> newTopics = topicConjunctionDto.getTopics().stream()
                    .map(topicDto -> topicRepository.findById(topicDto.getId())
                            .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId())))
                    .collect(Collectors.toSet());
            topicConjunction.updateTopics(newTopics);
            topicConjunction.setAssessment(assessment);
            topicConjunctionRepository.save(topicConjunction);
        }
        
        return new AssessmentDto(assessment);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeAssessment(Integer assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));
        assessment.remove();
        assessmentRepository.delete(assessment);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void assessmentSetStatus(Integer assessmentId, Assessment.Status status) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));
        assessment.setStatus(status);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> getAssessmentQuestions(Integer assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, assessmentId));

        return assessment.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> getTopicConjunctionQuestions(Integer executionId, TopicConjunctionDto topicConjunctionDto) {
        if (topicConjunctionDto.getId() != null) {
            TopicConjunction topicConjunction = topicConjunctionRepository.findById(topicConjunctionDto.getId()).orElseThrow(() -> new TutorException(TOPIC_CONJUNCTION_NOT_FOUND));

            return topicConjunction.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
        } else {
            Set<Topic> topics = new HashSet<>();
            topicConjunctionDto.getTopics().forEach(topicDto -> {
                Topic topic = topicRepository.findById(topicDto.getId()).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND));
                topics.add(topic);
            });
            if (!topics.isEmpty()) {
                return topics.stream()
                        .flatMap(topic -> topic.getQuestions().stream())
                        .filter(question -> topics.equals(question.getTopics()))
                        .distinct()
                        .map(QuestionDto::new)
                        .collect(Collectors.toList());
            } else {
                CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

                return questionRepository.findQuestions(courseExecution.getCourse().getId()).stream()
                        .filter(question -> question.getTopics().isEmpty())
                        .map(QuestionDto::new)
                        .collect(Collectors.toList());
            }
        }
    }

}

