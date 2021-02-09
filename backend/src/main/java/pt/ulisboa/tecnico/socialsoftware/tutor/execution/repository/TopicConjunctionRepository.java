package pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;

@Repository
@Transactional
public interface TopicConjunctionRepository extends JpaRepository<TopicConjunction, Integer> {

}