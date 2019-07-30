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

    //@Query(value = "select count(*) from (select max(answer_date) as answer_date, question_id, :id as user_id from answers where user_id = :id group by question_id) a join answers b on a.answer_date = b.answer_date and a.question_id = b.question_id and a.user_id = b.user_id join options c on b.option = c.option and b.question_id = c.question_id  where c.correct = true;", nativeQuery = true)
    //Integer getUniqueCorrectAnswers(Integer id);

    /*@Query("select count(distinct a.answerPK.question_id) from answers where a.answerPK.user_id = :id")
    TopicsStatsDto[] getTopicsStats(Integer id);*/
}

