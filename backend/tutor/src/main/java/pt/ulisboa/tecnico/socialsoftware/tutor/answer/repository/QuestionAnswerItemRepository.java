package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface QuestionAnswerItemRepository extends JpaRepository<QuestionAnswerItem, Integer> {
    @Query(value = "SELECT qai FROM QuestionAnswerItem qai WHERE qai.quizId = :quizId")
    List<QuestionAnswerItem> findQuestionAnswerItemsByQuizId(Integer quizId);

    @Query(value = "SELECT qai FROM QuestionAnswerItem qai WHERE qai.username = :username")
    List<QuestionAnswerItem> findQuestionAnswerItemsByUsername(String username);

    @Modifying
    @Query(value = "INSERT INTO question_answer_items (username, quiz_id, quiz_question_id, answer_date, time_taken, time_to_submission, option_id) values (:username, :quizId, :quizQuestionId, :answerDate, :timeTaken, :timeToSubmission, :optionId)",
            nativeQuery = true)
    void insertQuestionAnswerItem(String username, Integer quizId, Integer quizQuestionId,
                                  LocalDateTime answerDate, Integer timeTaken, Integer timeToSubmission, Integer optionId);

    @Modifying
    @Query(value = "INSERT INTO question_answer_items (username, quiz_id, quiz_question_id, answer_date, time_taken, time_to_submission) values (:username, :quizId, :quizQuestionId, :answerDate, :timeTaken, :timeToSubmission)",
            nativeQuery = true)
    void insertQuestionAnswerItemOptionIdNull(String username, Integer quizId, Integer quizQuestionId,
                                  LocalDateTime answerDate, Integer timeTaken, Integer timeToSubmission);

    @Modifying
    @Query(value = "UPDATE question_answer_items SET username = :newUsername WHERE username = :oldUsername",
            nativeQuery = true)
    void updateQuestionAnswerItemUsername(String oldUsername, String newUsername);

    @Query(value = "SELECT qai FROM QuestionAnswerItem qai WHERE qai.username LIKE 'Demo-Stu%' OR qai.username LIKE 'demo-stu%'")
    Set<QuestionAnswerItem> findDemoStudentQuestionAnswerItems();
}
