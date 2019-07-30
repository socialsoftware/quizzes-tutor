package pt.ulisboa.tecnico.socialsoftware.tutor.topic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.topic.domain.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

}