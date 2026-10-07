package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicSource;

import java.util.List;

@Repository
@Transactional
public interface TopicSourceRepository extends JpaRepository<TopicSource, Integer> {
    List<TopicSource> findByMaterialId(String materialId);
}
