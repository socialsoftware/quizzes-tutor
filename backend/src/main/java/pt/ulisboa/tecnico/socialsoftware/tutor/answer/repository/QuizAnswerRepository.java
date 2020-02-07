package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.util.Optional;

@Repository
@Transactional
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer> {

    @Query(value = "SELECT * FROM quiz_answers qa WHERE qa.user_id = :userId AND qa.quiz_id = :quizId", nativeQuery = true)
    Optional<QuizAnswer> findQuizAnswer(Integer quizId, Integer userId);
}

