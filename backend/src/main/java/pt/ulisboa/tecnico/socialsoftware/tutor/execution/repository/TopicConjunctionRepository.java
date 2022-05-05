package pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;

import java.util.List;

@Repository
@Transactional
public interface TopicConjunctionRepository extends JpaRepository<TopicConjunction, Integer> {
    @Query(value = "SELECT tp FROM TopicConjunction tp WHERE tp.assessment.id = :assessmentId")
    List<TopicConjunction> findAssessmentTopicConjunctions(int assessmentId);
}