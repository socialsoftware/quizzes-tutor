package pt.ulisboa.tecnico.socialsoftware.tutor.question.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
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

    @Transactional
    public QuestionDto findById(Integer questionId) {
        return questionRepository.findById(questionId).map(QuestionDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }

    @Transactional
    public List<QuestionDto> findAll(Integer pageIndex, Integer pageSize) {
        return questionRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public QuestionDto create(QuestionDto questionDto) {
        Question question = new Question(questionDto);
        this.entityManager.persist(question);
        return new QuestionDto(question);
    }

    @Transactional
    public void update(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.update(questionDto);
    }

    @Transactional
    public void remove(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.canRemove();
        entityManager.remove(question);
    }

    @Transactional
    public void questionSwitchActive(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.switchActive();
    }

    @Transactional
    public void setImageUrl(Integer questionId, String type) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));

        Image image = question.getImage();

        if (image != null) {
            image.setUrl(question.getId() + "." + type);

            return;
        }

        image = new Image();
        image.setUrl(question.getId() + "." + type);

        question.addImage(image);

        entityManager.persist(image);
    }
}

