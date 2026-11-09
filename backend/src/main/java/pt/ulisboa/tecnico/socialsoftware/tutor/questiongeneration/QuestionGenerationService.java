package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicNodeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.GenerationJobRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.QuestionGenerationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.ReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

/**
 * Teachers ask the generation service for questions; the drafts come back as Questions in
 * status SUBMITTED, tied to a QuestionGeneration that a teacher reviews like a student
 * submission. Nothing reaches quizzes before an APPROVE review.
 */
@Service
public class QuestionGenerationService {
    // More at once only lengthens the wait and the queue of the generation service
    private static final int MAX_QUESTIONS_PER_JOB = 10;
    // A job nobody collected for this long no longer counts as being written
    private static final int PENDING_WINDOW_MINUTES = 15;
    private static final ObjectMapper DRAFT_WRITER = new ObjectMapper();
    private static final int DEFAULT_QUESTIONS_PER_JOB = 5;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_TOPICS_PER_JOB = 10;
    // generation_jobs.topic is a varchar(255)
    private static final int MAX_TOPIC_LABEL_LENGTH = 250;
    private static final int MAX_LANGUAGE_LENGTH = 40;
    private static final int MAX_FOCUS_LENGTH = 500;

    @Autowired
    private AqgClient aqgClient;

    @Autowired
    private TopicService topicService;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private RequestThrottle requestThrottle;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionGenerationRepository questionGenerationRepository;

    @Autowired
    private GenerationJobRepository generationJobRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GenerationMaterialDto uploadMaterial(Integer executionId, String filename, byte[] content) {
        return new GenerationMaterialDto(
                aqgClient.uploadMaterial(getCourseExecution(executionId).getCourse().getId(), filename, content));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<GenerationMaterialDto> getMaterials(Integer executionId) {
        Integer courseId = getCourseExecution(executionId).getCourse().getId();
        Map<String, Integer> placed = topicService.countPlacedChunks(courseId);
        return aqgClient.listMaterials(courseId).stream()
                .map(material -> new GenerationMaterialDto(material, placed.get(material.id())))
                .collect(Collectors.toList());
    }

    /** The pieces of a material in reading order, each with the topic it is under, to distribute it over the topic tree. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<MaterialChunkDto> getMaterialChunks(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        Map<String, List<Integer>> topicsOfChunk = topicService.findTopicsOfChunks(courseIdOf(executionId), materialId);
        return aqgClient.getChunks(materialId).stream()
                .map(chunk -> new MaterialChunkDto(chunk, topicsOfChunk.get(chunk.id())))
                .collect(Collectors.toList());
    }

    /**
     * Puts the pieces of a material under topics, creating the new topics of `tree` on the way.
     * The tree says where every piece of the material goes; pieces in no node are left out of
     * question generation.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> distributeMaterial(Integer executionId, String materialId, TopicTreeDto tree) {
        checkMaterialOfCourse(executionId, materialId);
        Set<String> documentChunkIds = aqgClient.getChunks(materialId).stream()
                .map(AqgChunkDto::id)
                .collect(Collectors.toSet());
        return topicService.distributeMaterial(courseIdOf(executionId), materialId, documentChunkIds, tree);
    }

    /**
     * Reads a material again with the service's current parsers. Its pieces are cut anew, so the
     * old ones are taken from their topics and the teacher distributes the material again.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GenerationMaterialDto reprocessMaterial(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        topicService.detachMaterial(courseIdOf(executionId), materialId);
        return new GenerationMaterialDto(aqgClient.reprocessMaterial(materialId));
    }

    /**
     * Forgets a material. Its pieces leave their topics; the questions already written from it stay,
     * since they hold their own text.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteMaterial(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        aqgClient.deleteMaterial(materialId);
        topicService.detachMaterial(courseIdOf(executionId), materialId);
    }

    private Integer courseIdOf(Integer executionId) {
        return getCourseExecution(executionId).getCourse().getId();
    }

    private void checkMaterialOfCourse(Integer executionId, String materialId) {
        Integer courseId = courseIdOf(executionId);
        if (aqgClient.listMaterials(courseId).stream().noneMatch(material -> material.id().equals(materialId)))
            throw new TutorException(GENERATION_MATERIAL_NOT_FOUND, materialId);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public GenerationJobDto requestGeneration(Integer executionId, Integer requesterId, GenerationRequestDto request) {
        CourseExecution courseExecution = getCourseExecution(executionId);
        Integer courseId = courseExecution.getCourse().getId();

        int count = request.getCount() == null ? DEFAULT_QUESTIONS_PER_JOB : request.getCount();
        if (count < 1 || count > MAX_QUESTIONS_PER_JOB)
            throw new TutorException(GENERATION_INVALID_COUNT);
        checkNotTooMany(requesterId);

        List<Integer> topicIds = request.getTopicIds() == null ? List.of()
                : request.getTopicIds().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (topicIds.isEmpty())
            throw new TutorException(GENERATION_MISSING_TOPIC);
        if (topicIds.size() > MAX_TOPICS_PER_JOB)
            throw new TutorException(GENERATION_TOO_MANY_TOPICS, MAX_TOPICS_PER_JOB);
        List<Topic> topics = new ArrayList<>();
        for (Integer topicId : topicIds)
            topics.add(topicRepository.findTopicWithCourseById(topicId)
                    .filter(found -> found.getCourse().getId().equals(courseId))
                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId)));

        List<TopicSourceDto> sources = sourcesOfTopics(topicIds);
        if (sources.isEmpty())
            throw new TutorException(GENERATION_TOPIC_WITHOUT_SOURCES);

        String difficulty = request.getDifficulty() == null ? "MEDIUM" : request.getDifficulty();
        String groundingMode = request.getGroundingMode() == null ? "STRICT" : request.getGroundingMode();
        String language = normaliseLanguage(request.getLanguage());
        String focus = request.getFocus() == null || request.getFocus().isBlank() ? null : request.getFocus().trim();
        if (focus != null && focus.length() > MAX_FOCUS_LENGTH)
            throw new TutorException(GENERATION_INVALID_FOCUS);

        AqgJobDto aqgJob = aqgClient.generate(new AqgGenerateRequest(
                courseId, topicLabel(topics), difficulty, count, groundingMode, language, null,
                questionRepository.findRecentContents(courseId), chunkIdsOf(sources), focus));

        GenerationJob job = new GenerationJob(aqgJob.id(), executionId, requesterId, topicLabel(topics), topicIds,
                count, difficulty, groundingMode, aqgJob.status());
        job.setSource(materialIdsOf(sources), language);
        job.setFocus(focus);
        generationJobRepository.save(job);
        requestThrottle.recordGeneration(requesterId);
        return new GenerationJobDto(job);
    }

    /**
     * "Regenerate with review": records the teacher's REQUEST_CHANGES review and asks the
     * service to rewrite the question from the same materials, following the review. The
     * rewrite replaces the question once the job is done (see getGenerationJob).
     */
    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionGenerationDto regenerate(Integer questionGenerationId, Integer userId, String comment) {
        if (comment == null || comment.isBlank())
            throw new TutorException(REVIEW_MISSING_COMMENT);

        QuestionGeneration questionGeneration = getQuestionGeneration(questionGenerationId);
        if (questionGeneration.getStatus() != QuestionGeneration.Status.IN_REVIEW
                && questionGeneration.getStatus() != QuestionGeneration.Status.IN_REVISION)
            throw new TutorException(CANNOT_REVIEW_QUESTION_GENERATION);

        GenerationJob original = questionGeneration.getJob();
        if (original == null)
            throw new TutorException(GENERATION_CANNOT_REGENERATE);
        checkNotTooMany(userId);
        Integer courseId = questionGeneration.getCourseExecution().getCourse().getId();
        List<String> chunkIds = regenerationChunks(questionGeneration, original);

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setQuestionGenerationId(questionGenerationId);
        reviewDto.setUserId(userId);
        reviewDto.setComment(comment);
        reviewDto.setType(Review.Type.REQUEST_CHANGES.name());
        createReview(reviewDto);

        AqgJobDto aqgJob = aqgClient.generate(new AqgGenerateRequest(
                courseId, original.getTopic(), original.getDifficulty(), 1, original.getGroundingMode(),
                original.getLanguage(), new AqgGenerateRequest.Revision(currentDraft(questionGeneration), comment),
                questionRepository.findRecentContents(courseId), chunkIds, original.getFocus()));

        GenerationJob job = new GenerationJob(aqgJob.id(), original.getCourseExecutionId(), userId,
                original.getTopic(), original.getTopicIds(), 1, original.getDifficulty(), original.getGroundingMode(),
                aqgJob.status());
        job.setSource(original.getMaterialIds(), original.getLanguage());
        job.setFocus(original.getFocus());
        job.setRevisionOfId(questionGenerationId);
        generationJobRepository.save(job);

        requestThrottle.recordGeneration(userId);
        questionGeneration.startRegeneration(job.getId());
        return new QuestionGenerationDto(questionGeneration);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<GenerationJobDto> getGenerationJobs(Integer executionId) {
        return generationJobRepository.findByCourseExecutionIdOrderByIdDesc(executionId).stream()
                .map(GenerationJobDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Asks the generation service how the job is going and, the first time it is done,
     * turns its drafts into questions waiting for review. Polling is enough here: the
     * service never needs credentials to call back into the Tutor.
     */
    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public GenerationJobDto getGenerationJob(Integer executionId, Integer jobId) {
        GenerationJob job = generationJobRepository.findByIdForUpdate(jobId)
                .filter(found -> found.getCourseExecutionId().equals(executionId))
                .orElseThrow(() -> new TutorException(GENERATION_JOB_NOT_FOUND, jobId));

        if (job.getStatus() == GenerationJob.Status.REQUESTED) {
            AqgJobDto aqgJob = aqgClient.getJob(job.getAqgJobId());
            job.setAqgStatus(aqgJob.status());

            if ("DONE".equals(aqgJob.status())) {
                if (job.getRevisionOfId() != null)
                    importRevision(job, aqgJob);
                else
                    importQuestions(job, aqgJob);
            } else if ("FAILED".equals(aqgJob.status())) {
                job.markFailed(aqgJob.error());
                if (job.getRevisionOfId() != null)
                    getQuestionGeneration(job.getRevisionOfId()).abandonRegeneration();
            }
        }

        return new GenerationJobDto(job);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionGenerationDto> getCourseExecutionQuestionGenerations(Integer executionId) {
        return questionGenerationRepository.findByCourseExecution(executionId).stream()
                .map(QuestionGenerationDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReviewDto createReview(ReviewDto reviewDto) {
        if (reviewDto.getQuestionGenerationId() == null)
            throw new TutorException(REVIEW_MISSING_QUESTION_GENERATION);
        if (reviewDto.getUserId() == null)
            throw new TutorException(REVIEW_MISSING_USER);

        QuestionGeneration questionGeneration = getQuestionGeneration(reviewDto.getQuestionGenerationId());
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, reviewDto.getUserId()));

        Review review = new Review(user, questionGeneration, reviewDto);

        if (questionGeneration.getStatus() != QuestionGeneration.Status.IN_REVIEW
                && questionGeneration.getStatus() != QuestionGeneration.Status.IN_REVISION)
            throw new TutorException(CANNOT_REVIEW_QUESTION_GENERATION);
        questionGeneration.setStatus(reviewDto.getType());

        questionGeneration.addReview(review);
        reviewRepository.save(review);
        return new ReviewDto(review);
    }

    @Retryable(value = {SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReviewDto> getQuestionGenerationReviews(Integer questionGenerationId) {
        return reviewRepository.findReviewsByGenerationId(questionGenerationId).stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    private void importQuestions(GenerationJob job, AqgJobDto aqgJob) {
        CourseExecution courseExecution = getCourseExecution(job.getCourseExecutionId());
        Integer courseId = courseExecution.getCourse().getId();
        Set<Topic> topics = new HashSet<>(topicRepository.findAllById(job.getTopicIds()));

        int imported = 0;
        int skipped = 0;
        for (AqgJobDto.Outcome outcome : aqgJob.outcomes() == null ? List.<AqgJobDto.Outcome>of() : aqgJob.outcomes()) {
            if (outcome.question() == null) {
                skipped++;
                continue;
            }

            Question question = createDraftQuestion(courseId, outcome.question(), job.getDifficulty());
            if (!topics.isEmpty())
                question.updateTopics(topics);

            QuestionGeneration questionGeneration = new QuestionGeneration(courseExecution, question, job);
            questionGeneration.recordDraft(draftJson(outcome.question()), DateHandler.now());
            questionGeneration.setGenerationDetails(
                    aqgJob.modelId(),
                    aqgJob.promptVersion(),
                    aqgJob.groundingMode(),
                    outcome.retries() == null ? 0 : outcome.retries(),
                    "NEEDS_HUMAN_ATTENTION".equals(outcome.status()),
                    outcome.question().explanation(),
                    outcome.sourceChunkIds() == null ? new ArrayList<>() : outcome.sourceChunkIds());
            questionGenerationRepository.save(questionGeneration);
            imported++;
        }

        job.markImported(imported, skipped);
    }

    private void importRevision(GenerationJob job, AqgJobDto aqgJob) {
        QuestionGeneration questionGeneration = getQuestionGeneration(job.getRevisionOfId());
        AqgJobDto.Outcome outcome = aqgJob.outcomes() == null || aqgJob.outcomes().isEmpty()
                ? null : aqgJob.outcomes().get(0);

        if (outcome == null || outcome.question() == null) {
            questionGeneration.abandonRegeneration();
            job.markImported(0, 1);
            return;
        }

        // Rewritten in place: the Question keeps its id, topics and review log
        questionGeneration.getQuestion().update(draftDto(outcome.question(), job.getDifficulty()));
        questionGeneration.recordDraft(draftJson(outcome.question()), DateHandler.now());
        questionGeneration.finishRegeneration();
        questionGeneration.setGenerationDetails(
                aqgJob.modelId(),
                aqgJob.promptVersion(),
                aqgJob.groundingMode(),
                outcome.retries() == null ? 0 : outcome.retries(),
                "NEEDS_HUMAN_ATTENTION".equals(outcome.status()),
                outcome.question().explanation(),
                outcome.sourceChunkIds() == null ? new ArrayList<>() : outcome.sourceChunkIds());
        job.markImported(1, 0);
    }

    /** The pieces under the topics and their subtopics, each once, topic after topic in the order asked for. */
    private List<TopicSourceDto> sourcesOfTopics(List<Integer> topicIds) {
        Map<String, TopicSourceDto> distinct = new LinkedHashMap<>();
        for (Integer topicId : topicIds)
            topicService.findSourcesOfSubtree(topicId).forEach(source -> distinct.putIfAbsent(source.getChunkId(), source));
        return new ArrayList<>(distinct.values());
    }

    /** The names of the topics, as the list of requests and the model's prompt show them. */
    private String topicLabel(List<Topic> topics) {
        String label = topics.stream().map(Topic::getName).collect(Collectors.joining(", "));
        return label.length() <= MAX_TOPIC_LABEL_LENGTH ? label : label.substring(0, MAX_TOPIC_LABEL_LENGTH - 3) + "...";
    }

    private List<String> chunkIdsOf(List<TopicSourceDto> sources) {
        return sources.stream().map(TopicSourceDto::getChunkId).collect(Collectors.toList());
    }

    private List<String> materialIdsOf(List<TopicSourceDto> sources) {
        return sources.stream().map(TopicSourceDto::getMaterialId).distinct().collect(Collectors.toList());
    }

    /**
     * The text to rewrite a question from: the pieces it was found to rest on, or else what is
     * under its topic now.
     */
    /** A teacher cannot send request after request: a pause between them and a cap on the ones still being written. */
    private void checkNotTooMany(Integer teacherId) {
        requestThrottle.checkGeneration(teacherId);
        long pending = generationJobRepository.countByRequesterIdAndStatusAndCreationDateAfter(
                teacherId, GenerationJob.Status.REQUESTED, DateHandler.now().minusMinutes(PENDING_WINDOW_MINUTES));
        if (pending >= requestThrottle.getMaxPendingJobs())
            throw new TutorException(GENERATION_TOO_MANY_PENDING, requestThrottle.getMaxPendingJobs());
    }

    private List<String> regenerationChunks(QuestionGeneration questionGeneration, GenerationJob original) {
        if (!questionGeneration.getSourceChunkIds().isEmpty())
            return new ArrayList<>(questionGeneration.getSourceChunkIds());
        List<Integer> existing = original.getTopicIds().stream().filter(topicRepository::existsById).collect(Collectors.toList());
        List<TopicSourceDto> sources = sourcesOfTopics(existing);
        if (!sources.isEmpty())
            return chunkIdsOf(sources);
        throw new TutorException(GENERATION_CANNOT_REGENERATE);
    }

    /** A draft as the model wrote it, kept for the analysis (stem, options with the correct one, explanation). */
    private String draftJson(AqgJobDto.Question draft) {
        try {
            return DRAFT_WRITER.writeValueAsString(draft);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /** The question as the service wrote it, to be rewritten following a review. */
    private AqgJobDto.Question currentDraft(QuestionGeneration questionGeneration) {
        Question question = questionGeneration.getQuestion();
        List<AqgJobDto.Option> options = new ArrayList<>();
        if (question.getQuestionDetails() instanceof MultipleChoiceQuestion details)
            details.getOptions().forEach(option -> options.add(new AqgJobDto.Option(option.getContent(), option.isCorrect())));
        String explanation = questionGeneration.getExplanation() == null ? "" : questionGeneration.getExplanation();
        return new AqgJobDto.Question(question.getContent(), options, explanation);
    }

    private String normaliseLanguage(String language) {
        if (language == null || language.isBlank())
            return null;
        String trimmed = language.trim();
        if (trimmed.length() > MAX_LANGUAGE_LENGTH)
            throw new TutorException(GENERATION_INVALID_LANGUAGE);
        return trimmed;
    }

    private Question createDraftQuestion(Integer courseId, AqgJobDto.Question draft, String difficulty) {
        QuestionDto created = questionService.createQuestion(courseId, draftDto(draft, difficulty));
        Question question = questionRepository.findById(created.getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, created.getId()));
        question.setOrigin(Question.Origin.GENERATED);
        return question;
    }

    private QuestionDto draftDto(AqgJobDto.Question draft, String difficulty) {
        List<OptionDto> options = new ArrayList<>();
        for (AqgJobDto.Option draftOption : draft.options()) {
            OptionDto option = new OptionDto();
            option.setContent(draftOption.content());
            option.setCorrect(draftOption.correct());
            options.add(option);
        }
        MultipleChoiceQuestionDto details = new MultipleChoiceQuestionDto();
        details.setOptions(options);

        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(titleFor(draft.stem(), difficulty));
        questionDto.setContent(draft.stem());
        questionDto.setStatus(Question.Status.SUBMITTED.name());
        questionDto.setQuestionDetailsDto(details);
        return questionDto;
    }

    /**
     * The question's name: the start of the stem and, in brackets, the difficulty asked for, so the
     * teacher knows it while reviewing and in the question bank (a question has no difficulty of its own
     * until students answer it).
     */
    private String titleFor(String stem, String difficulty) {
        String oneLine = stem.replaceAll("\\s+", " ").trim();
        String suffix = difficulty == null || difficulty.isBlank() ? ""
                : " (" + difficulty.charAt(0) + difficulty.substring(1).toLowerCase() + ")";
        int room = MAX_TITLE_LENGTH - suffix.length();
        String head = oneLine.length() <= room ? oneLine : oneLine.substring(0, room - 3) + "...";
        return head + suffix;
    }

    private CourseExecution getCourseExecution(Integer executionId) {
        return courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
    }

    private QuestionGeneration getQuestionGeneration(Integer questionGenerationId) {
        return questionGenerationRepository.findById(questionGenerationId)
                .orElseThrow(() -> new TutorException(QUESTION_GENERATION_NOT_FOUND, questionGenerationId));
    }
}
