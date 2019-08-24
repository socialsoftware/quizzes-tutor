package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "select MAX(number) from questions", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "select * from questions q where q.active = true", nativeQuery = true)
    List<Question> getActiveQuestions();

    @Query(value = "select COUNT(*) from questions q where q.active = true", nativeQuery = true)
    Integer getTotalActiveQuestions();
}