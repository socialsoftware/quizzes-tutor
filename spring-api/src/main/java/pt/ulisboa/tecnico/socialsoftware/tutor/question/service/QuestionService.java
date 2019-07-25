package pt.ulisboa.tecnico.socialsoftware.tutor.question.service;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    public void update(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        question.setContent(questionDto.getContent());
        question.setDifficulty(questionDto.getDifficulty());
        //question.setImage(new Image(questionId, questionDto.getImage()));
        question.setActive(questionDto.getActive());
        //question.setOptions(questionDto.getOptions().stream().map(Option::new).collect(Collectors.toList()));
    }

    @Transactional
    public void create(QuestionDto question) {
        this.entityManager.persist(new Question(question));
    }

    public List<Question> findAll(Integer pageIndex, Integer pageSize) {
        return questionRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }

    @Transactional
    public void delete(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        entityManager.remove(question);
    }
}

