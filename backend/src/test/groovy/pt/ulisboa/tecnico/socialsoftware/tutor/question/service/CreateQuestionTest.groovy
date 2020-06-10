package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import spock.lang.Unroll

@DataJpaTest
class CreateQuestionTest extends SpockTest {

    def "create a multiple choice question with no image and one option"() {
        given: "a questionDto"
        def questionDto = new MultipleChoiceQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.createQuestion(course.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        result.getCourse().getName() == COURSE_1_NAME
        course.getQuestions().contains(result)
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.getCorrect()

    }

    def "create a multiple choice question with image and two options"() {
        given: "a questionDto"
        def questionDto = new MultipleChoiceQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)
        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.createQuestion(course.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        result.getOptions().size() == 2
    }

    def "create two multiple choice questions"() {
        given: "a questionDto"
        def questionDto = new MultipleChoiceQuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        when: 'are created two questions'
        questionService.createQuestion(course.getId(), questionDto)
        questionDto.setKey(null)
        questionService.createQuestion(course.getId(), questionDto)

        then: "the two questions are created with the correct numbers"
        questionRepository.count() == 2L
        def resultOne = questionRepository.findAll().get(0)
        def resultTwo = questionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
    }

    def "create a code fill in question"() {
        given: "a questionDto with 1 fill in spot with 1 option"
        CodeFillInQuestionDto questionDto = new CodeFillInQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setLanguage(CodeFillInQuestion.Language.Java.name())

        FillInSpotDto fillInSpotDto = new FillInSpotDto();
        OptionDto optionDto = new OptionDto();
        optionDto.setContent(OPTION_1_CONTENT);
        optionDto.setCorrect(true);
        fillInSpotDto.getOptions().add(optionDto);
        fillInSpotDto.setSequence(1);

        questionDto.getFillInSpots().add(fillInSpotDto);

        when:
        def rawResult = questionService.createQuestion(course.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof CodeFillInQuestionDto
        def result = (CodeFillInQuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getFillInSpots().size() == 1
        result.getFillInSpots().get(0).getOptions().size() == 1

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = (CodeFillInQuestion) questionRepository.findById(result.getId()).get()
        repoResult.getId() == result.getId()
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getFillInSpots().size() == 1
        repoResult.getCourse().getName() == COURSE_1_NAME
    }

    def "cannot create a code fill in question without fillin spots"() {
        given: "a questionDto"
        CodeFillInQuestionDto questionDto = new CodeFillInQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED
    }

    def "cannot create a code fill in question with fillin spots without options"() {
        given: "a questionDto with 1 fill in spot without options"
        CodeFillInQuestionDto questionDto = new CodeFillInQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        FillInSpotDto fillInSpotDto = new FillInSpotDto();
        questionDto.getFillInSpots().add(fillInSpotDto);


        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }

    def "cannot create a code fill in question with fillin spots without correct options"() {
        given: "a questionDto with 1 fill in spot without options"
        CodeFillInQuestionDto questionDto = new CodeFillInQuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        FillInSpotDto fillInSpotDto = new FillInSpotDto();
        OptionDto optionDto = new OptionDto();
        optionDto.setContent(OPTION_1_CONTENT);
        optionDto.setCorrect(false);
        questionDto.getFillInSpots().add(fillInSpotDto);


        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }

    @Unroll
    def "fail to create multiple choice question for invalid/non-existent course (#nonExistentId)"(Integer nonExistentId) {
        given: "any multiple choice question dto"
        def questionDto = new MultipleChoiceQuestionDto()

        when:
        questionService.createQuestion(nonExistentId, questionDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_NOT_FOUND

        where:
        nonExistentId << [-1, 0, 200]
    }

    @Unroll
    def "failt to create code fill in question for invalid/non-existent course (#nonExistentId)"(Integer nonExistentId) {
        given: "any code fill in question dto"
        def questionDto = new CodeFillInQuestionDto()

        when:
        questionService.createQuestion(nonExistentId, questionDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_NOT_FOUND

        where:
        nonExistentId << [-1, 0, 200]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
