package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionApplicationalService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionApplicationalService.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    public void removeCourseNonQuizQuestions(int courseId) {
        List<Question> questions = questionRepository.findCourseQuestions(courseId);

        int counterKeep = 0;
        for (Question question : questions) {
            try {
                questionService.removeQuestion(question.getId());
            } catch (TutorException tutorException) {
                logger.info("{}: {} ({})", tutorException.getErrorMessage(), ++counterKeep, question.getId());
            }
        }
    }
}
