package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

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
    public void create(QuizDTO quiz) {
        entityManager.persist(new Quiz(quiz));
    }

    @Transactional
    public void update(Integer quizId, QuizDTO quizRequest) {
        Quiz quiz = findById(quizId);
        quiz.setType(quizRequest.getType());
        quiz.setDate(quizRequest.getDate());
        quiz.setSeries(quizRequest.getSeries());
        quiz.setVersion(quizRequest.getVersion());
        quiz.setYear(quizRequest.getYear());
        quiz.setTitle(quizRequest.getTitle());
        quiz.setQuestions(quizRequest.getQuestions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new Question(entry.getValue()))));
    }

    @Transactional
    public void delete(Integer quizId) {
        entityManager.remove(findById(quizId));
    }
}
