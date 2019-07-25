package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service;

import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public List<Quiz> findAll(int pageIndex, int pageSize) {
        return quizRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }


    public Quiz findById(Integer quizId) {
        return this.quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId));
    }

    @Transactional
    public Quiz create(QuizDto quizDto) {
        Quiz quiz = new Quiz(quizDto);
        entityManager.persist(quiz);
        return quiz;
    }

    @Transactional
    public void delete(Integer quizId) {
        entityManager.remove(findById(quizId));
    }

    @Transactional
    public Quiz generateStudentQuiz(Integer studentId, Integer quizSize) {
        Quiz quiz = new Quiz();

        // TODO: to include knowhow about the student in the future
        quiz.generate(quizSize, questionRepository.getActiveQuestions());

        entityManager.persist(quiz);

        return quiz;
    }
}
