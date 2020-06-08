package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerItemRepository extends JpaRepository<QuizAnswerItem, Integer> {
    @Query(value = "SELECT qaq FROM QuizAnswerItem qaq WHERE qaq.quizId = :quizId")
    List<QuizAnswerItem> findQuizAnswers(Integer quizId);
}
