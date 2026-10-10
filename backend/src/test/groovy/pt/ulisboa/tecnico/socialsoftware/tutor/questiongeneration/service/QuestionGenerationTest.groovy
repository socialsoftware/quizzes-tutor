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
        dto.setTopicId(overrides.containsKey('topicId') ? overrides.topicId : topic.getId())
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
        21    | 'http'    || GENERATION_INVALID_COUNT
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
                                             new AqgJobDto.Outcome('INSUFFICIENT_CONTEXT', null, 0, ['no context'], [])])

        when:
        def result = questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())

        then: "the job is imported, counting the draft that had nothing to import"
        result.getStatus() == 'IMPORTED'
        result.getImportedCount() == 2
        result.getSkippedCount() == 1

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
        chunks*.topicId() == [topic.getId(), topic.getId(), null]
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

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
