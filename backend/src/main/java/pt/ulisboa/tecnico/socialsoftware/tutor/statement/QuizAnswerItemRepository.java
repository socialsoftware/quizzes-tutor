package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;

import java.util.List;
import java.util.Set;

@Repository
public interface QuizAnswerItemRepository extends JpaRepository<QuizAnswerItem, Integer> {
    @Query(value = "SELECT qai.quizId FROM QuizAnswerItem qai")
    Set<Integer> findQuizzesToWrite();

    @Query(value = "SELECT qaq FROM QuizAnswerItem qaq WHERE qaq.quizId = :quizId")
    List<QuizAnswerItem> findQuizAnswerItemsByQuizId(Integer quizId);
}
