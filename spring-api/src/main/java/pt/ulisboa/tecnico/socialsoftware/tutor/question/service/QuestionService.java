package pt.ulisboa.tecnico.socialsoftware.tutor.question.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public Question findById(Integer questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }

    @Transactional
    public List<QuestionDto> findAll(Integer pageIndex, Integer pageSize) {
        return questionRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void create(QuestionDto question) {
        this.entityManager.persist(new Question(question));
    }

    @Transactional
    public void update(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.update(questionDto);
    }

    // TODO: Check that it can be deleted, it is has answers, it cannot
    @Transactional
    public void delete(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        entityManager.remove(question);
    }

}

