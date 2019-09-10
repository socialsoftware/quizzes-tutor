package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "select * from quizzes q where q.type != 'STUDENT'", nativeQuery = true)
    List<Quiz> findAllNonGenerated();

    @Query(value = "select * from quizzes q where q.type != 'STUDENT' and q.year == :year", nativeQuery = true)
    List<Quiz> findAvailableTeacherQuizzes(Integer year);

    @Query(value = "select MAX(number) from quizzes", nativeQuery = true)
    Integer getMaxQuizNumber();

    @Query(value = "select * from quizzes q where q.number = :number", nativeQuery = true)
    Optional<Quiz> findByNumber(Integer number);
}