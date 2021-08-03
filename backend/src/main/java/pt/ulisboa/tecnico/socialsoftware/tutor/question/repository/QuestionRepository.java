package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @EntityGraph(attributePaths = {"course"})
    Optional<Question> findQuestionWithCourseById(int id);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId", nativeQuery = true)
    List<Question> findQuestions(int courseId);

    @Query(value = "SELECT question FROM Question question WHERE question.course.id = :courseId AND (question.title LIKE %:content% OR question.content LIKE %:content%)")
    Set<Question> findQuestionsByContent(int courseId, String content);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> findAvailableQuestions(int courseId);

    @Query(value = "SELECT count(*) FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getAvailableQuestionsSize(Integer courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.key = :key", nativeQuery = true)
    Optional<Question> findByKey(Integer key);
}