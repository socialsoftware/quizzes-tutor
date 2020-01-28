package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "SELECT * FROM questions q, courses c WHERE c.name = :courseName AND q.course_id = c.id", nativeQuery = true)
    List<Question> findQuestions(String courseName);

    @Query(value = "SELECT * FROM questions q, courses c WHERE c.name = :courseName AND q.course_id = c.id AND q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> findAvailableQuestions(String courseName);

    @Query(value = "SELECT count(*) FROM questions q, courses c WHERE c.name = :courseName AND q.course_id = c.id AND q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getAvailableQuestionsSize(String courseName);

    @Query(value = "SELECT MAX(number) FROM questions", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "SELECT * FROM questions q WHERE q.id = :id", nativeQuery = true)
    Optional<Question> findByNumber(Integer id);
}