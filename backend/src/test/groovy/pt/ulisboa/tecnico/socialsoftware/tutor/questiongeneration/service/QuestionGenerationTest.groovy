package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class QuestionGenerationTest extends SpockTest {
    def teacher
    def topic

    def setup() {
        aqgClient.reset()
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        topic = new Topic()
        topic.setName('HTTP')
        topic.setCourse(externalCourse)
        topic.addSource('material-1', 'material-1:0')
        topic.addSource('material-1', 'material-1:1')
        topicRepository.save(topic)
    }

    def request(Map overrides = [:]) {
        def dto = new GenerationRequestDto()
        if (overrides.containsKey('topicIds'))
            dto.setTopicIds(overrides.topicIds)
        else
            dto.setTopicId(overrides.containsKey('topicId') ? overrides.topicId : topic.getId())
        if (overrides.containsKey('difficulty'))
            dto.setDifficulty(overrides.difficulty)
        dto.setCount(overrides.containsKey('count') ? overrides.count : 2)
        return dto
    }

    def aqgJob(String status, List outcomes = [], String error = null) {
        return new AqgJobDto('aqg-1', status, 'mcq-v1', 'ollama/llama3.1:8b', 'STRICT', outcomes, error)
    }

    def draftOutcome(String status = 'OK') {
        def question = new AqgJobDto.Question('What does 404 mean?', [
                new AqgJobDto.Option('Resource not found', true),
                new AqgJobDto.Option('Server error', false),
                new AqgJobDto.Option('Unauthorized', false),
                new AqgJobDto.Option('Redirect', false)], 'It is the missing resource code')
        return new AqgJobDto.Outcome(status, question, 1, [], ['material-1:0'])
    }

    def requestedJob() {
        aqgClient.generateReply = aqgJob('PENDING')
        return questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())
    }

    def "a generation request is forwarded to the service and remembered"() {
        given:
        aqgClient.generateReply = aqgJob('PENDING')

        when:
        def result = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        then: "the service was asked for the course, the topic and the pieces of documents under it"
        aqgClient.lastGenerateRequest.courseId() == externalCourse.getId()
        aqgClient.lastGenerateRequest.topic() == 'HTTP'
        aqgClient.lastGenerateRequest.chunkIds() == ['material-1:0', 'material-1:1']
        aqgClient.lastGenerateRequest.groundingMode() == 'STRICT'

        and: "the job is stored as requested"
        def job = generationJobRepository.findAll().get(0)
        job.getAqgJobId() == 'aqg-1'
        job.getStatus() == GenerationJob.Status.REQUESTED
        job.getCourseExecutionId() == externalCourseExecution.getId()
        job.getRequesterId() == teacher.getId()
        job.getTopicId() == topic.getId()
        job.getMaterialIds() == ['material-1']
        result.getId() == job.getId()
    }

    @Unroll
    def "invalid request: count=#count topic=#which"() {
        given:
        def empty = new Topic()
        empty.setName('Empty')
        empty.setCourse(externalCourse)
        topicRepository.save(empty)
        def topicId = [http: topic.getId(), empty: empty.getId(), none: null, unknown: -1][which]

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(),
                request(count: count, topicId: topicId))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        count | which     || errorMessage
        0     | 'http'    || GENERATION_INVALID_COUNT
        11    | 'http'    || GENERATION_INVALID_COUNT
        20    | 'http'    || GENERATION_INVALID_COUNT
        2     | 'none'    || GENERATION_MISSING_TOPIC
        2     | 'unknown' || TOPIC_NOT_FOUND
        2     | 'empty'   || GENERATION_TOPIC_WITHOUT_SOURCES
    }

    def "the pieces of the subtopics come after the topic's own, in tree order"() {
        given:
        def second = new Topic()
        second.setName('Status codes')
        second.setCourse(externalCourse)
        second.setParentId(topic.getId())
        second.setSequence(1)
        second.addSource('material-2', 'material-2:5')
        topicRepository.save(second)
        def first = new Topic()
        first.setName('Methods')
        first.setCourse(externalCourse)
        first.setParentId(topic.getId())
        first.setSequence(0)
        first.addSource('material-2', 'material-2:3')
        topicRepository.save(first)
        aqgClient.generateReply = aqgJob('PENDING')

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        then:
        aqgClient.lastGenerateRequest.chunkIds() == ['material-1:0', 'material-1:1', 'material-2:3', 'material-2:5']
        generationJobRepository.findAll().get(0).getMaterialIds() == ['material-1', 'material-2']
    }

    def "a job still running imports nothing"() {
        given:
        def job = requestedJob()
        aqgClient.jobReply = aqgJob('RUNNING')

        when:
        def result = questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        result.getStatus() == 'REQUESTED'
        result.getGenerationStatus() == 'RUNNING'
        questionGenerationRepository.findAll().isEmpty()
        questionRepository.findAll().isEmpty()
    }

    def "a finished job turns its drafts into submitted questions waiting for review"() {
        given:
        def job = requestedJob()
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome('OK'), draftOutcome('NEEDS_HUMAN_ATTENTION'),
                                             new AqgJobDto.Outcome('INSUFFICIENT_CONTEXT', null, 0, ['insufficient context: the text is only a heading'], [])])

        when:
        def result = questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then: "the job is imported, counting the draft that had nothing to import"
        result.getStatus() == 'IMPORTED'
        result.getImportedCount() == 2
        result.getSkippedCount() == 1
        result.getSkippedReason() == 'the text is only a heading'

        and: "the questions are hidden from quizzes and marked as generated"
        def questions = questionRepository.findAll()
        questions.size() == 2
        questions.every { it.getStatus() == Question.Status.SUBMITTED }
        questions.every { it.getOrigin() == Question.Origin.GENERATED }
        questionRepository.findAvailableQuestions(externalCourse.getId()).isEmpty()

        and: "each question has four options with one correct"
        def details = questions.get(0).getQuestionDetails()
        details.getOptions().size() == 4
        details.getOptions().count { it.isCorrect() } == 1

        and: "how it was generated is kept next to each question"
        def generations = questionGenerationRepository.findAll()
        generations.size() == 2
        generations.every { it.getStatus() == QuestionGeneration.Status.IN_REVIEW }
        generations.every { it.getModelId() == 'ollama/llama3.1:8b' && it.getPromptVersion() == 'mcq-v1' }
        generations.every { it.getSourceChunkIds() == ['material-1:0'] }
        generations.count { it.needsHumanAttention() } == 1
        generations.get(0).getExplanation() == 'It is the missing resource code'

        and: "the questions are filed under the topic they were asked about"
        questions.every { it.getTopics()*.getId() == [topic.getId()] }
    }

    def otherTopic(String name, List<String> chunkIds, Integer parentId = null) {
        def other = new Topic()
        other.setName(name)
        other.setCourse(externalCourse)
        other.setParentId(parentId)
        chunkIds.each { other.addSource('material-1', it) }
        topicRepository.save(other)
        return other
    }

    def "several topics are asked about at once: their pieces go together, each once, and so do their names"() {
        given: 'a second topic that shares a piece with the first'
        def methods = otherTopic('Methods', ['material-1:1', 'material-1:2'])
        aqgClient.generateReply = aqgJob('PENDING')

        when:
        def result = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(),
                request(topicIds: [topic.getId(), methods.getId()]))

        then: "the service gets the pieces of both topics, the shared one once"
        aqgClient.lastGenerateRequest.chunkIds() == ['material-1:0', 'material-1:1', 'material-1:2']
        aqgClient.lastGenerateRequest.topic() == 'HTTP, Methods'

        and: "the job remembers the topics"
        def job = generationJobRepository.findAll().get(0)
        job.getTopicIds() == [topic.getId(), methods.getId()]
        job.getTopic() == 'HTTP, Methods'
        result.getTopicIds() == [topic.getId(), methods.getId()]
    }

    def "the questions of a request about several topics are filed under all of them"() {
        given:
        def methods = otherTopic('Methods', ['material-1:2'])
        aqgClient.generateReply = aqgJob('PENDING')
        def job = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(),
                request(topicIds: [topic.getId(), methods.getId()]))
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome()])

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        def question = questionRepository.findAll().get(0)
        question.getTopics()*.getId() as Set == [topic.getId(), methods.getId()] as Set
    }

    @Unroll
    def "a request about several topics is refused: #what"() {
        given:
        def empty = otherTopic('Empty', [])
        def otherCourse = new pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course('Other', pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course.Type.EXTERNAL)
        courseRepository.save(otherCourse)
        def foreign = new Topic()
        foreign.setName('Foreign')
        foreign.setCourse(otherCourse)
        foreign.addSource('material-9', 'material-9:0')
        topicRepository.save(foreign)
        def ids = ids_.collect { [http: topic.getId(), empty: empty.getId(), foreign: foreign.getId(), unknown: -1][it] }

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request(topicIds: ids))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == error

        where:
        what                            | ids_                  || error
        'none'                          | []                    || GENERATION_MISSING_TOPIC
        'a topic of another course'     | ['http', 'foreign']   || TOPIC_NOT_FOUND
        'a topic that does not exist'   | ['http', 'unknown']   || TOPIC_NOT_FOUND
        'only topics without pieces'    | ['empty']             || GENERATION_TOPIC_WITHOUT_SOURCES
    }

    def "at most ten topics for each request"() {
        given:
        def ids = (1..11).collect { otherTopic("T${it}", ["material-1:${it + 10}".toString()]).getId() }

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request(topicIds: ids))

        then:
        thrown(TutorException)
    }

    def "a repeated topic counts once, and a request with one topic still works through topicId"() {
        given:
        aqgClient.generateReply = aqgJob('PENDING')

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(),
                request(topicIds: [topic.getId(), topic.getId()]))

        then:
        generationJobRepository.findAll().get(0).getTopicIds() == [topic.getId()]
        aqgClient.lastGenerateRequest.chunkIds() == ['material-1:0', 'material-1:1']
    }

    def "a job from before there could be several topics still has its one topic"() {
        given: 'a job that only has topic_id'
        def job = new GenerationJob('old-job', externalCourseExecution.getId(), teacher.getId(), 'HTTP', [topic.getId()], 1, 'MEDIUM', 'STRICT', 'DONE')
        job.@topicIds = []

        expect:
        job.getTopicIds() == [topic.getId()]
    }

    def "the name of a generated question carries the difficulty that was asked for"() {
        given:
        aqgClient.generateReply = aqgJob('PENDING')
        def job = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(),
                request(difficulty: difficulty))
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome()])

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        questionRepository.findAll().get(0).getTitle() == "What does 404 mean? (${shown})"
        questionGenerationService.getCourseExecutionQuestionGenerations(externalCourseExecution.getId()).get(0).getDifficulty() == difficulty

        where:
        difficulty | shown
        'EASY'     | 'Easy'
        'MEDIUM'   | 'Medium'
        'HARD'     | 'Hard'
    }

    def "a long stem is cut so that the name with the difficulty still fits"() {
        given:
        aqgClient.generateReply = aqgJob('PENDING')
        def job = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request(difficulty: 'HARD'))
        def long_ = new AqgJobDto.Question('Why ' + ('word ' * 60), [
                new AqgJobDto.Option('A', true), new AqgJobDto.Option('B', false),
                new AqgJobDto.Option('C', false), new AqgJobDto.Option('D', false)], 'because')
        aqgClient.jobReply = aqgJob('DONE', [new AqgJobDto.Outcome('OK', long_, 0, [], ['material-1:0'])])

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        def title = questionRepository.findAll().get(0).getTitle()
        title.length() <= 100
        title.endsWith('... (Hard)')
    }

    def "importing is done once even if the job is read again"() {
        given:
        def job = requestedJob()
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome()])

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        questionGenerationRepository.findAll().size() == 1
        aqgClient.getJobCalls == 1
    }

    def "a failed job records why"() {
        given:
        def job = requestedJob()
        aqgClient.jobReply = aqgJob('FAILED', [], 'ConnectionError: ollama unreachable')

        when:
        def result = questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then:
        result.getStatus() == 'FAILED'
        result.getError() == 'ConnectionError: ollama unreachable'
        questionGenerationRepository.findAll().isEmpty()
    }

    def "jobs are listed newest first and only for their own course execution"() {
        given:
        requestedJob()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v1', 'm', 'STRICT', [], null)
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        when:
        def result = questionGenerationService.getGenerationJobs(externalCourseExecution.getId())
        def other = questionGenerationService.getGenerationJobs(externalCourseExecution.getId() + 1)

        then:
        result.size() == 2
        result.get(0).getId() > result.get(1).getId()
        other.isEmpty()
    }

    def "materials are shown with the teacher facing field names and how many pieces are under a topic"() {
        given:
        aqgClient.materials = [new AqgMaterialDto('material-1', externalCourse.getId(), 'lecture.pdf', 'READY', 7, 'pymupdf4llm', 1.5, null)]

        when:
        def result = questionGenerationService.getMaterials(externalCourseExecution.getId())

        then:
        result.size() == 1
        result.get(0).getFilename() == 'lecture.pdf'
        result.get(0).getChunkCount() == 7
        result.get(0).getParser() == 'pymupdf4llm'
        result.get(0).getPlacedChunks() == 2
    }

    def "a job of another course execution is not found"() {
        given:
        def job = requestedJob()

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId() + 1, job.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_JOB_NOT_FOUND
    }

    def reviewOf(Integer questionGenerationId, Review.Type type) {
        def reviewDto = new ReviewDto()
        reviewDto.setQuestionGenerationId(questionGenerationId)
        reviewDto.setUserId(teacher.getId())
        reviewDto.setComment(REVIEW_1_COMMENT)
        reviewDto.setType(type.name())
        return reviewDto
    }

    def importedGeneration() {
        def job = requestedJob()
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome()])
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())
        return questionGenerationRepository.findAll().get(0)
    }

    @Unroll
    def "review of type #type leaves the generation #generationStatus and the question #questionStatus"() {
        given:
        def generation = importedGeneration()

        when:
        questionGenerationService.createReview(reviewOf(generation.getId(), type))

        then:
        def result = questionGenerationRepository.findAll().get(0)
        result.getStatus() == generationStatus
        result.getQuestion().getStatus() == questionStatus
        def review = reviewRepository.findAll().get(0)
        review.getQuestionGeneration() == result
        review.getUser() == teacher
        review.getType() == type

        where:
        type                        || generationStatus                          || questionStatus
        Review.Type.APPROVE         || QuestionGeneration.Status.APPROVED        || Question.Status.AVAILABLE
        Review.Type.REJECT          || QuestionGeneration.Status.REJECTED        || Question.Status.SUBMITTED
        Review.Type.REQUEST_CHANGES || QuestionGeneration.Status.IN_REVISION     || Question.Status.SUBMITTED
        Review.Type.COMMENT         || QuestionGeneration.Status.IN_REVIEW       || Question.Status.SUBMITTED
    }

    @Unroll
    def "a generated question can be #type without a comment"() {
        given:
        def generation = importedGeneration()
        def review = reviewOf(generation.getId(), type)
        review.setComment(null)

        when:
        questionGenerationService.createReview(review)

        then:
        questionGenerationRepository.findAll().get(0).getStatus() == status
        reviewRepository.findAll().get(0).getComment() == ''

        where:
        type               || status
        Review.Type.APPROVE || QuestionGeneration.Status.APPROVED
        Review.Type.REJECT  || QuestionGeneration.Status.REJECTED
    }

    def "asking for changes needs a comment"() {
        given:
        def generation = importedGeneration()
        def review = reviewOf(generation.getId(), Review.Type.REQUEST_CHANGES)
        review.setComment(' ')

        when:
        questionGenerationService.createReview(review)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == REVIEW_MISSING_COMMENT
    }

    def "an approved question reaches the pool quizzes draw from"() {
        given:
        def generation = importedGeneration()

        when:
        questionGenerationService.createReview(reviewOf(generation.getId(), Review.Type.APPROVE))

        then:
        questionRepository.findAvailableQuestions(externalCourse.getId()).size() == 1
    }

    def "a generation that was approved cannot be reviewed again"() {
        given:
        def generation = importedGeneration()
        questionGenerationService.createReview(reviewOf(generation.getId(), Review.Type.APPROVE))

        when:
        questionGenerationService.createReview(reviewOf(generation.getId(), Review.Type.REJECT))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REVIEW_QUESTION_GENERATION
    }

    def "a review needs a generation and a reviewer"() {
        given:
        def generation = importedGeneration()
        def noGeneration = reviewOf(generation.getId(), Review.Type.APPROVE)
        noGeneration.setQuestionGenerationId(null)
        def noUser = reviewOf(generation.getId(), Review.Type.APPROVE)
        noUser.setUserId(null)

        when:
        questionGenerationService.createReview(noGeneration)

        then:
        thrown(TutorException)

        when:
        questionGenerationService.createReview(noUser)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == REVIEW_MISSING_USER
    }

    def "reviews of a generation are listed"() {
        given:
        def generation = importedGeneration()
        questionGenerationService.createReview(reviewOf(generation.getId(), Review.Type.COMMENT))

        when:
        def result = questionGenerationService.getQuestionGenerationReviews(generation.getId())

        then:
        result.size() == 1
        result.get(0).getQuestionGenerationId() == generation.getId()
        result.get(0).getQuestionSubmissionId() == null
    }

    def "a generated question cannot be deleted from under its review"() {
        given:
        def generation = importedGeneration()

        when:
        questionService.removeQuestion(generation.getQuestion().getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_DELETE_SUBMITTED_QUESTION
    }

    def "the language and the course's existing questions go with the request"() {
        given:
        importedGeneration()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)
        def dto = request()
        dto.setLanguage(' Portuguese ')

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), dto)

        then:
        aqgClient.lastGenerateRequest.language() == 'Portuguese'
        aqgClient.lastGenerateRequest.existingStems() == ['What does 404 mean?']
        aqgClient.lastGenerateRequest.revision() == null
        generationJobRepository.findAll().every { it.getMaterialIds() == ['material-1'] }
    }

    def rewrittenOutcome() {
        def question = new AqgJobDto.Question('Which status code reports a missing resource?', [
                new AqgJobDto.Option('404', true),
                new AqgJobDto.Option('500', false),
                new AqgJobDto.Option('401', false),
                new AqgJobDto.Option('301', false)], 'A missing resource is 404')
        return new AqgJobDto.Outcome('OK', question, 0, [], ['material-1:1'])
    }

    def "regenerating records the review and asks for a rewrite of the same question"() {
        given:
        def generation = importedGeneration()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)

        when:
        def result = questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Ask it the other way round')

        then: "the review is a REQUEST_CHANGES with the teacher's comment"
        def review = reviewRepository.findAll().get(0)
        review.getType() == Review.Type.REQUEST_CHANGES
        review.getComment() == 'Ask it the other way round'

        and: "the service rewrites one question from the text it rested on, given the old one and the review"
        def sent = aqgClient.lastGenerateRequest
        sent.count() == 1
        sent.chunkIds() == ['material-1:0']
        sent.topic() == 'HTTP'
        sent.revision().review() == 'Ask it the other way round'
        sent.revision().previous().stem() == 'What does 404 mean?'
        sent.revision().previous().options().count { it.correct() } == 1

        and: "the generation waits for the rewrite"
        result.getStatus() == 'REGENERATING'
        def job = generationJobRepository.findAll().find { it.getAqgJobId() == 'aqg-2' }
        job.getRevisionOfId() == generation.getId()
        result.getRegenerationJobId() == job.getId()
    }

    def "a finished rewrite replaces the question and puts it back up for review"() {
        given: "a generated question, filed under its topic"
        def generation = importedGeneration()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)
        def regenerating = questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Ask it the other way round')
        aqgClient.jobReply = new AqgJobDto('aqg-2', 'DONE', 'mcq-v2', 'nim', 'STRICT', [rewrittenOutcome()], null)

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), regenerating.getRegenerationJobId())

        then: "still one generation, now with the rewritten question"
        def generations = questionGenerationRepository.findAll()
        generations.size() == 1
        generations.get(0).getStatus() == QuestionGeneration.Status.IN_REVIEW
        generations.get(0).getQuestion().getContent() == 'Which status code reports a missing resource?'
        generations.get(0).getPromptVersion() == 'mcq-v2'
        generations.get(0).getRegenerationJobId() == null

        and: "the rewrite keeps the topic"
        generations.get(0).getQuestion().getTopics()*.getName() == ['HTTP']

        and: "the old question is gone and the review log is kept"
        questionRepository.findAll().size() == 1
        reviewRepository.findAll().size() == 1
    }

    @Unroll
    def "a rewrite that #what gives the question back to the teacher"() {
        given:
        def generation = importedGeneration()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)
        def regenerating = questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Ask it the other way round')
        aqgClient.jobReply = reply

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), regenerating.getRegenerationJobId())

        then:
        def result = questionGenerationRepository.findAll().get(0)
        result.getStatus() == QuestionGeneration.Status.IN_REVISION
        result.getQuestion().getContent() == 'What does 404 mean?'

        where:
        what              | reply
        'fails'           | new AqgJobDto('aqg-2', 'FAILED', 'mcq-v2', 'nim', 'STRICT', [], 'Timeout')
        'returns nothing' | new AqgJobDto('aqg-2', 'DONE', 'mcq-v2', 'nim', 'STRICT', [new AqgJobDto.Outcome('INSUFFICIENT_CONTEXT', null, 0, [], [])], null)
    }

    def "a question without the pieces it rested on is regenerated from what is under its topic now"() {
        given:
        def generation = importedGeneration()
        generation.getSourceChunkIds().clear()
        questionGenerationRepository.save(generation)
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Clearer please')

        then:
        aqgClient.lastGenerateRequest.chunkIds() == ['material-1:0', 'material-1:1']
    }

    def "a question with no text left to rest on cannot be regenerated"() {
        given:
        def generation = importedGeneration()
        generation.getSourceChunkIds().clear()
        questionGenerationRepository.save(generation)
        topicRepository.findById(topic.getId()).get().getSources().clear()

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Clearer please')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_CANNOT_REGENERATE
    }

    def "the focus goes with the request and with a later regeneration"() {
        given:
        aqgClient.generateReply = aqgJob('PENDING')
        def dto = request()
        dto.setFocus(' sign rules ')

        when:
        def job = questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), dto)

        then:
        aqgClient.lastGenerateRequest.focus() == 'sign rules'

        when: "a draft of that job is regenerated"
        aqgClient.jobReply = aqgJob('DONE', [draftOutcome()])
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)
        questionGenerationService.regenerate(questionGenerationRepository.findAll().get(0).getId(), teacher.getId(), 'Harder')

        then: "the rewrite keeps the focus"
        aqgClient.lastGenerateRequest.focus() == 'sign rules'
    }

    def "a focus longer than 500 characters is refused"() {
        given:
        def dto = request()
        dto.setFocus('x' * 501)

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), dto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_INVALID_FOCUS
    }

    def "the pieces of a material come with the topic each one is under"() {
        given:
        aqgClient.addMaterial('material-1', externalCourse.getId(), ['Networks', 'Networks > HTTP', 'Networks > DNS'])

        when:
        def chunks = questionGenerationService.getMaterialChunks(externalCourseExecution.getId(), 'material-1')

        then:
        chunks*.id() == ['material-1:0', 'material-1:1', 'material-1:2']
        chunks*.heading() == ['Networks', 'Networks > HTTP', 'Networks > DNS']
        chunks*.topicIds() == [[topic.getId()], [topic.getId()], []]
    }

    def "reprocessing a material takes its old pieces from the topics"() {
        given:
        aqgClient.addMaterial('material-1', externalCourse.getId(), ['Networks', 'Networks > HTTP'])

        when:
        def result = questionGenerationService.reprocessMaterial(externalCourseExecution.getId(), 'material-1')

        then:
        result.getId() == 'material-1'
        aqgClient.reprocessedMaterialId == 'material-1'
        topicRepository.findById(topic.getId()).get().getSources().isEmpty()
    }

    def "deleting a material takes its pieces from the topics and forgets it in the service"() {
        given:
        aqgClient.addMaterial('material-1', externalCourse.getId(), ['Networks', 'Networks > HTTP'])

        when:
        questionGenerationService.deleteMaterial(externalCourseExecution.getId(), 'material-1')

        then:
        aqgClient.deletedMaterialId == 'material-1'
        topicRepository.findById(topic.getId()).get().getSources().isEmpty()
    }

    @Unroll
    def "#what is only for materials of the course"() {
        given:
        aqgClient.addMaterial('other-course-material', externalCourse.getId() + 100, ['Elsewhere'])

        when:
        action(questionGenerationService, externalCourseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MATERIAL_NOT_FOUND

        where:
        what           | action
        'reading'      | { service, execution -> service.getMaterialChunks(execution, 'other-course-material') }
        'reprocessing' | { service, execution -> service.reprocessMaterial(execution, 'other-course-material') }
        'deleting'     | { service, execution -> service.deleteMaterial(execution, 'other-course-material') }
        'distributing' | { service, execution -> service.distributeMaterial(execution, 'other-course-material', null) }
    }

    def "regenerating needs a comment"() {
        given:
        def generation = importedGeneration()

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), ' ')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == REVIEW_MISSING_COMMENT
    }

    def "an approved question cannot be regenerated"() {
        given:
        def generation = importedGeneration()
        questionGenerationService.createReview(reviewOf(generation.getId(), Review.Type.APPROVE))

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Too late')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REVIEW_QUESTION_GENERATION
    }

    // ---------------------------------------------------------------- what is kept for the analysis

    def "the draft as the model wrote it is kept, with when it arrived"() {
        when:
        def generation = importedGeneration()

        then:
        def draft = new groovy.json.JsonSlurper().parseText(generation.getOriginalDraft())
        draft.stem == 'What does 404 mean?'
        draft.options.size() == 4
        draft.options.count { it.correct } == 1
        draft.options.find { it.correct }.content == 'Resource not found'
        draft.explanation == 'It is the missing resource code'
        generation.getModelDraft() == generation.getOriginalDraft()
        generation.getImportedAt() != null
        generation.getRegenerationCount() == 0
        generation.getRegeneratedAt() == null
    }

    def "a regeneration keeps the original draft and records the new model version"() {
        given:
        def generation = importedGeneration()
        def original = generation.getOriginalDraft()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)
        def regenerating = questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Ask it the other way round')
        aqgClient.jobReply = new AqgJobDto('aqg-2', 'DONE', 'mcq-v2', 'nim', 'STRICT', [rewrittenOutcome()], null)

        when:
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), regenerating.getRegenerationJobId())

        then:
        def result = questionGenerationRepository.findAll().get(0)
        result.getOriginalDraft() == original
        new groovy.json.JsonSlurper().parseText(result.getModelDraft()).stem == 'Which status code reports a missing resource?'
        result.getRegenerationCount() == 1
        result.getRegeneratedAt() != null
    }

    def "a teacher's edit after the model wrote it shows as a difference with the model draft"() {
        given:
        def generation = importedGeneration()

        when: "the teacher changes the text of the question on the Questions page"
        generation.getQuestion().setContent('What does the HTTP status code 404 mean?')

        then:
        def draft = new groovy.json.JsonSlurper().parseText(generation.getModelDraft())
        draft.stem != generation.getQuestion().getContent()
    }

    def "a review of a generated question is dated by the server, not by what the browser says"() {
        given:
        def generation = importedGeneration()
        def dto = reviewOf(generation.getId(), Review.Type.APPROVE)
        dto.setCreationDate('2001-01-01T10:00:00Z')
        def before = DateHandler.now().minusSeconds(5)

        when:
        questionGenerationService.createReview(dto)

        then:
        def review = reviewRepository.findAll().get(0)
        review.getCreationDate() != null
        review.getCreationDate().isAfter(before)
    }

    def "the review made by a regeneration has a date too"() {
        given:
        def generation = importedGeneration()
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'nim', 'STRICT', [], null)

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Ask it the other way round')

        then:
        reviewRepository.findAll().get(0).getCreationDate() != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
