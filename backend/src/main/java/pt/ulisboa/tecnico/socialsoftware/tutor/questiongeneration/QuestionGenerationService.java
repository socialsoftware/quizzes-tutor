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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicSourceRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.GenerationJobRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.QuestionGenerationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.ReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private static final int MAX_QUESTIONS_PER_JOB = 20;
    private static final int DEFAULT_QUESTIONS_PER_JOB = 5;
    private static final int MAX_TITLE_LENGTH = 100;
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
    private TopicSourceRepository topicSourceRepository;

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
        return aqgClient.listMaterials(getCourseExecution(executionId).getCourse().getId()).stream()
                .map(GenerationMaterialDto::new)
                .collect(Collectors.toList());
    }

    /** The headings of a course material, to ask for questions about part of it. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AqgSectionDto> getSections(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        return aqgClient.getSections(materialId);
    }

    /** Rebuilds a material's sections with the service's current parsers and heading cleanup. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GenerationMaterialDto reprocessMaterial(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        return new GenerationMaterialDto(aqgClient.reprocessMaterial(materialId));
    }

    /** The headings of a course material with the text under them, to edit its sections. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AqgOutlineNodeDto> getOutline(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        return aqgClient.getOutline(materialId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AqgParagraphDto> getSectionText(Integer executionId, String materialId, String path) {
        checkMaterialOfCourse(executionId, materialId);
        return aqgClient.getSectionText(materialId, path);
    }

    /**
     * Changes the sections of a material and moves the topic links with them: a renamed section
     * keeps its topics, a merged one is now part of the section before it, and a section that was
     * cut keeps both halves linked to the same topics (the teacher then moves one of them).
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AqgOutlineEditResultDto editOutline(Integer executionId, String materialId, AqgOutlineEditDto edit) {
        checkMaterialOfCourse(executionId, materialId);
        AqgOutlineEditResultDto result = aqgClient.editOutline(materialId, edit);

        Map<String, List<String>> pathMap = result.pathMap() == null ? Map.of() : result.pathMap();
        if (!pathMap.isEmpty()) {
            topicSourceRepository.findByMaterialId(materialId).stream()
                    .filter(source -> pathMap.containsKey(source.getSectionPath()))
                    .collect(Collectors.toList())
                    .forEach(source -> source.getTopic()
                            .followSource(materialId, source.getSectionPath(), pathMap.get(source.getSectionPath())));
        }
        return result;
    }

    /** A proposal for the course's topic tree from the sections of a document; nothing is saved. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicTreeDto suggestTopicTree(Integer executionId, String materialId) {
        checkMaterialOfCourse(executionId, materialId);
        Integer courseId = getCourseExecution(executionId).getCourse().getId();

        Map<String, Integer> existingTopicIds = new LinkedHashMap<>();
        topicRepository.findTopics(courseId).forEach(topic -> existingTopicIds.putIfAbsent(topic.getName(), topic.getId()));
        return TopicTreeSuggester.suggest(materialId, aqgClient.getSections(materialId), existingTopicIds);
    }

    private void checkMaterialOfCourse(Integer executionId, String materialId) {
        Integer courseId = getCourseExecution(executionId).getCourse().getId();
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

        String topicName = request.getTopic();
        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findTopicWithCourseById(request.getTopicId())
                    .filter(found -> found.getCourse().getId().equals(courseId))
                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, request.getTopicId()));
            topicName = topic.getName();
        }
        if (topicName == null || topicName.isBlank())
            throw new TutorException(GENERATION_MISSING_TOPIC);

        List<String> materialIds = request.getMaterialIds() == null ? List.of() : request.getMaterialIds();
        List<String> sections = request.getSections() == null ? List.of() : request.getSections();
        Map<String, List<String>> materialSections = new LinkedHashMap<>();
        if (Boolean.TRUE.equals(request.getFromTopic())) {
            if (request.getTopicId() == null)
                throw new TutorException(GENERATION_MISSING_TOPIC);
            materialSections = sectionsByMaterial(topicService.findSourcesOfSubtree(request.getTopicId()));
            materialIds = new ArrayList<>(materialSections.keySet());
            sections = List.of();
        }
        if (materialIds.isEmpty())
            throw new TutorException(GENERATION_MISSING_MATERIALS);

        String difficulty = request.getDifficulty() == null ? "MEDIUM" : request.getDifficulty();
        String groundingMode = request.getGroundingMode() == null ? "STRICT" : request.getGroundingMode();
        String language = normaliseLanguage(request.getLanguage());
        String focus = request.getFocus() == null || request.getFocus().isBlank() ? null : request.getFocus().trim();
        if (focus != null && focus.length() > MAX_FOCUS_LENGTH)
            throw new TutorException(GENERATION_INVALID_FOCUS);

        AqgJobDto aqgJob = aqgClient.generate(new AqgGenerateRequest(
                courseId, topicName, difficulty, count, groundingMode, materialIds,
                language, null, questionRepository.findRecentContents(courseId), sections, focus, materialSections));

        GenerationJob job = new GenerationJob(aqgJob.id(), executionId, requesterId, topicName, request.getTopicId(),
                count, difficulty, groundingMode, aqgJob.status());
        job.setSource(materialIds, language);
        job.setScope(sections, focus);
        job.setMaterialSections(materialSections);
        generationJobRepository.save(job);
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
        Integer courseId = questionGeneration.getCourseExecution().getCourse().getId();
        List<String> materialIds = sourceMaterials(original, courseId);

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setQuestionGenerationId(questionGenerationId);
        reviewDto.setUserId(userId);
        reviewDto.setComment(comment);
        reviewDto.setType(Review.Type.REQUEST_CHANGES.name());
        createReview(reviewDto);

        AqgJobDto aqgJob = aqgClient.generate(new AqgGenerateRequest(
                courseId, original.getTopic(), original.getDifficulty(), 1, original.getGroundingMode(),
                materialIds, original.getLanguage(),
                new AqgGenerateRequest.Revision(currentDraft(questionGeneration), comment),
                questionRepository.findRecentContents(courseId), original.getSections(), original.getFocus(),
                original.getMaterialSections()));

        GenerationJob job = new GenerationJob(aqgJob.id(), original.getCourseExecutionId(), userId,
                original.getTopic(), original.getTopicId(), 1, original.getDifficulty(), original.getGroundingMode(),
                aqgJob.status());
        job.setSource(materialIds, original.getLanguage());
        job.setScope(original.getSections(), original.getFocus());
        job.setMaterialSections(original.getMaterialSections());
        job.setRevisionOfId(questionGenerationId);
        generationJobRepository.save(job);

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
        Topic topic = job.getTopicId() == null ? null : topicRepository.findById(job.getTopicId()).orElse(null);

        int imported = 0;
        int skipped = 0;
        for (AqgJobDto.Outcome outcome : aqgJob.outcomes() == null ? List.<AqgJobDto.Outcome>of() : aqgJob.outcomes()) {
            if (outcome.question() == null) {
                skipped++;
                continue;
            }

            Question question = createDraftQuestion(courseId, outcome.question());
            if (topic != null)
                question.updateTopics(Set.of(topic));

            QuestionGeneration questionGeneration = new QuestionGeneration(courseExecution, question, job);
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
        questionGeneration.getQuestion().update(draftDto(outcome.question()));
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

    private Map<String, List<String>> sectionsByMaterial(List<TopicSourceDto> sources) {
        Map<String, List<String>> byMaterial = new LinkedHashMap<>();
        for (TopicSourceDto source : sources)
            byMaterial.computeIfAbsent(source.getMaterialId(), id -> new ArrayList<>()).add(source.getSectionPath());
        if (byMaterial.isEmpty())
            throw new TutorException(GENERATION_TOPIC_WITHOUT_SOURCES);
        return byMaterial;
    }

    /**
     * The materials the question came from. Jobs requested before they were recorded fall back
     * to every material of the course the service has finished reading.
     */
    private List<String> sourceMaterials(GenerationJob job, Integer courseId) {
        if (!job.getMaterialIds().isEmpty())
            return job.getMaterialIds();
        List<String> ready = aqgClient.listMaterials(courseId).stream()
                .filter(material -> "READY".equals(material.status()))
                .map(AqgMaterialDto::id)
                .collect(Collectors.toList());
        if (ready.isEmpty())
            throw new TutorException(GENERATION_CANNOT_REGENERATE);
        return ready;
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

    private Question createDraftQuestion(Integer courseId, AqgJobDto.Question draft) {
        QuestionDto created = questionService.createQuestion(courseId, draftDto(draft));
        Question question = questionRepository.findById(created.getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, created.getId()));
        question.setOrigin(Question.Origin.GENERATED);
        return question;
    }

    private QuestionDto draftDto(AqgJobDto.Question draft) {
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
        questionDto.setTitle(titleFor(draft.stem()));
        questionDto.setContent(draft.stem());
        questionDto.setStatus(Question.Status.SUBMITTED.name());
        questionDto.setQuestionDetailsDto(details);
        return questionDto;
    }

    private String titleFor(String stem) {
        String oneLine = stem.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= MAX_TITLE_LENGTH ? oneLine : oneLine.substring(0, MAX_TITLE_LENGTH - 3) + "...";
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
