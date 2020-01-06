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
    @Query(value = "SELECT * FROM questions q INNER JOIN courses c ON c.id = q.course_id WHERE c.name = :name", nativeQuery = true)
    List<Question> findCourseQuestions(String name);

    @Query(value = "SELECT * FROM questions q INNER JOIN courses c ON c.id = q.course_id WHERE c.name = :name AND q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> findCourseAvailableQuestions(String name);

    @Query(value = "SELECT count(*) FROM questions q INNER JOIN courses c ON c.id = q.course_id WHERE c.name = :name AND q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getCourseAvailableQuestionsSize();

    @Query(value = "SELECT MAX(number) FROM questions", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "SELECT * FROM questions WHERE q.number = :number", nativeQuery = true)
    Optional<Question> findByNumber(Integer number);
}