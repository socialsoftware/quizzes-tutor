package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "select MAX(number) from quizzes", nativeQuery = true)
    Integer getMaxQuizNumber();
}