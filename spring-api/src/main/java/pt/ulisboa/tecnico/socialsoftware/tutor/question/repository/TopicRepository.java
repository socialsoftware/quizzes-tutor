package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.util.List;

@Repository
@Transactional
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Query(value = "SELECT * FROM topics t INNER JOIN courses c ON c.id = t.course_id WHERE c.name = :courseName", nativeQuery = true)
    List<Topic> findCourseTopics(String courseName);

    @Query(value = "SELECT * FROM topics t INNER JOIN courses c ON c.id = t.course_id WHERE c.name = :courseName AND t.name = :name", nativeQuery = true)
    Topic findCourseTopicByName(String courseName, String name);
}