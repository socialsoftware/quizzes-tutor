package com.example.tutor.question;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.image.Image;
import com.example.tutor.option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Component
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
    public void update(Integer questionId, QuestionDTO questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        question.setContent(questionDto.getContent());
        question.setDifficulty(questionDto.getDifficulty());
        //question.setImage(new Image(questionId, questionDto.getImage()));
        question.setActive(questionDto.getActive());
        //question.setOptions(questionDto.getOptions().stream().map(Option::new).collect(Collectors.toList()));
    }

    @Transactional
    public void create(QuestionDTO question) {
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

