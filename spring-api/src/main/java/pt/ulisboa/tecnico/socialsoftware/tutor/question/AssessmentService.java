package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.QUESTION_NOT_FOUND;

@Service
public class AssessmentService {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    @Autowired
    private AssessmentRepository assessmentRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<AssessmentDto> findAll() {
        return assessmentRepository.findAll().stream().map(AssessmentDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<AssessmentDto> findAllAvailable() {
        return assessmentRepository.findAll().stream()
                .filter(assessment -> assessment.getStatus() == Assessment.Status.AVAILABLE)
                .map(AssessmentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AssessmentDto createAssessment(AssessmentDto assessmentDto) {
        Assessment assessment = new Assessment(assessmentDto);
        this.entityManager.persist(assessment);
        return new AssessmentDto(assessment);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateAssessment(Integer assessmentId, AssessmentDto assessmentDto) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, assessmentId));
        assessment.update(assessmentDto);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeAssessment(Integer assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, assessmentId));
        assessment.remove();
        entityManager.remove(assessment);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void assessmentSetStatus(Integer assessmentId, Assessment.Status status) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, assessmentId));
        assessment.setStatus(status);
    }

/*    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importAssessment(String assessmentXML) {
        AssessmentXmlImport xmlImporter = new AssessmentXmlImport();

        xmlImporter.importAssessment(assessmentXML, this);
    }*/
}

