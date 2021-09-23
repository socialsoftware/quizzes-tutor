package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TarGZip;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_FOUND;

@RestController
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Value("${export.dir}")
    private String exportDir;

    @GetMapping("/quizzes/executions/{executionId}/non-generated")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuizDto> findNonGeneratedQuizzes(@PathVariable int executionId) {
        return quizService.findNonGeneratedQuizzes(executionId);
    }

    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PostMapping("/quizzes/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuizDto createQuiz(@PathVariable int executionId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.createQuiz(executionId, quiz);
    }

    @PutMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto updateQuiz(@PathVariable Integer quizId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.updateQuiz(quizId, quiz);
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void deleteQuiz(@PathVariable Integer quizId) {
        quizService.removeQuiz(quizId);
    }

    @GetMapping(value = "/quizzes/{quizId}/export")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void exportQuiz(HttpServletResponse response, @PathVariable Integer quizId) throws IOException {
        answerService.writeQuizAnswers(quizId);

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        response.setHeader("Content-Disposition", "attachment; filename=file.tar.gz");
        response.setContentType("application/tar.gz");
        String sourceFolder = exportDir + "/quiz-" + quizId;
        File file = new File(sourceFolder);
        file.mkdir();
        this.quizService.createQuizXmlDirectory(quiz.getId(), sourceFolder);
        TarGZip tGzipDemo = new TarGZip(sourceFolder);
        tGzipDemo.createTarFile();
        response.getOutputStream().write(Files.readAllBytes(Paths.get(sourceFolder + ".tar.gz")));
        response.flushBuffer();

        deleteDirectory(file);
        deleteDirectory(new File(sourceFolder + ".tar.gz"));
    }

    @PostMapping("/quizzes/{quizId}/populate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto populateWithQuizAnswers(@PathVariable Integer quizId) {
        return this.quizService.populateWithQuizAnswers(quizId);
    }

    @PostMapping("/quizzes/{quizId}/unpopulate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto removeNonFilledQuizAnswers(@PathVariable Integer quizId) {
        return this.quizService.removeNonFilledQuizAnswers(quizId);
    }

    @GetMapping("/quizzes/{quizId}/answers")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizAnswersDto getQuizAnswers(@PathVariable Integer quizId) {
        answerService.writeQuizAnswers(quizId);
        return this.quizService.getQuizAnswers(quizId);
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}