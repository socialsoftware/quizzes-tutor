package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private QuestionService questionService;

    @Value("${figures.dir}")
    private String figuresDir;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<QuestionDto> getQuestions(@RequestParam(value = "page", defaultValue = "-1") int pageIndex, @RequestParam(value = "size", defaultValue = "-1") int pageSize){
        if (pageIndex == -1 && pageSize == -1) {
            pageIndex = 0;
            pageSize = Integer.MAX_VALUE;
        }

        return this.questionService.findAllQuestions(pageIndex, pageSize);
    }

    @GetMapping("/questions/available")
    public List<QuestionDto> getAvailableQuestions(){
        return this.questionService.findAvailableQuestions();
    }

    @GetMapping("/questions/{questionId}")
    public QuestionDto getQuestion(@PathVariable Integer questionId) {
        return this.questionService.findQuestionById(questionId);
    }

    @PostMapping("/questions")
    public QuestionDto createQuestion(@Valid @RequestBody QuestionDto question) {
        logger.debug("createQuestion title: {}, content: {}, options: {}: ",
                question.getTitle(), question.getContent(),
                question.getOptions().stream().map(optionDto -> optionDto.getId() + " : " + optionDto.getContent() + " : " + optionDto.getCorrect())
                        .collect(Collectors.joining("\n")));
        question.setStatus(Question.Status.AVAILABLE.name());
        return this.questionService.createQuestion(question);
    }

    @PutMapping("/questions/{questionId}")
    public QuestionDto updateQuestion(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
        return this.questionService.updateQuestion(questionId, question);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity removeQuestion(@PathVariable Integer questionId) throws IOException {
        logger.debug("removeQuestion questionId: {}: ", questionId);
        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;

        questionService.removeQuestion(questionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/set-status")
    public ResponseEntity questionSetStatus(@PathVariable Integer questionId, @Valid @RequestBody String status) {
        logger.debug("questionSetStatus questionId: {}: ", questionId);
        questionService.questionSetStatus(questionId, Question.Status.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/questions/{questionId}/image")
    public String uploadImage(@PathVariable Integer questionId, @RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("uploadImage  questionId: {}: , filename: {}", questionId, file.getContentType());

        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        questionService.uploadImage(questionId, type);

        url = questionService.findQuestionById(questionId).getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    @PutMapping("/questions/{questionId}/topics")
    public ResponseEntity updateQuestionTopics(@PathVariable Integer questionId, @RequestBody TopicDto[] topics) {
        logger.debug("updateQuestionTopics  questionId: {}: , topics: {}", questionId, Arrays.toString(topics));

        questionService.updateQuestionTopics(questionId, topics);

        return ResponseEntity.ok().build();
    }


    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }

}