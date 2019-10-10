package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "select MAX(number) from questions", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "select * from questions q where q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> getAvailableQuestions();

    @Query(value = "select COUNT(*) from questions q where q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getTotalAvailableQuestions();

    @Query(value = "select * from questions q where q.number = :number", nativeQuery = true)
    Optional<Question> findByNumber(Integer number);
}