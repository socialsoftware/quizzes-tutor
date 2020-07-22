package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@RestController
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    @Value("${figures.dir}")
    private String figuresDir;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/courses/{courseId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getCourseQuestions(@PathVariable int courseId) {
        return this.questionService.findQuestions(courseId);
    }

    @GetMapping(value = "/courses/{courseId}/questions/export")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public void exportQuestions(HttpServletResponse response, @PathVariable int courseId) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=file.zip");
        response.setContentType("application/zip");
        response.getOutputStream().write(this.questionService.exportCourseQuestions(courseId).toByteArray());

        response.flushBuffer();
    }

    @GetMapping("/courses/{courseId}/questions/available")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getAvailableQuestions(@PathVariable int courseId) {
        return this.questionService.findAvailableQuestions(courseId);
    }

    @PostMapping("/courses/{courseId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionDto createQuestion(@PathVariable int courseId, @Valid @RequestBody QuestionDto question) {
        question.setStatus(Question.Status.AVAILABLE.name());
        return this.questionService.createQuestion(courseId, question);
    }

    @GetMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto getQuestion(@PathVariable Integer questionId) {
        return this.questionService.findQuestionById(questionId);
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto updateQuestion(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
        return this.questionService.updateQuestion(questionId, question);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public void removeQuestion(@PathVariable Integer questionId) throws IOException {
        logger.debug("removeQuestion questionId: {}: ", questionId);
        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;

        questionService.removeQuestion(questionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }
    }

    @PostMapping("/questions/{questionId}/set-status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public void questionSetStatus(@PathVariable Integer questionId, @Valid @RequestBody String status) {
        logger.debug("questionSetStatus questionId: {}: ", questionId);
        questionService.questionSetStatus(questionId, Question.Status.valueOf(status));
    }

    @PutMapping("/questions/{questionId}/image")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
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
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public void updateQuestionTopics(@PathVariable Integer questionId, @RequestBody TopicDto[] topics) {
        questionService.updateQuestionTopics(questionId, topics);
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}