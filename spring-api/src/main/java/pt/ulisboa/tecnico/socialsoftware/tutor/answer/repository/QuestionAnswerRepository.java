package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Integer> {

    @Query(value = "select count(distinct a.quiz_id) from quiz_answers a where a.user_id = :id", nativeQuery = true)
    Integer getTotalQuizzes(Integer id);

    @Query(value = "select count(*) from question_answers a, quiz_answers q where q.user_id = :id and a.quiz_answer_id = q.id", nativeQuery = true)
    Integer getTotalAnswers(Integer id);

    @Query(value = "select count(distinct a.quiz_question_id) from question_answers a, quiz_answers q where q.user_id = :id and a.quiz_answer_id = q.id", nativeQuery = true)
    Integer getUniqueAnswers(Integer id);

}

