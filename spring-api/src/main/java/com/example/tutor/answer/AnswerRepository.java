package com.example.tutor.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    @Query(value = "select count(distinct a.quiz_id) from answers a where a.user_id = :id", nativeQuery = true)
    Integer getTotalQuizzes(Integer id);

    @Query(value = "select count(*) from answers a where a.user_id = :id", nativeQuery = true)
    Integer getTotalAnswers(Integer id);

    @Query(value = "select count(distinct a.question_id) from answers a where a.user_id = :id", nativeQuery = true)
    Integer getUniqueAnswers(Integer id);

    @Query(value = "select count(distinct a.question_id) from answers a, options o where a.user_id = :id and a.question_id = o.question_id and a.option = o.option and o.correct = true", nativeQuery = true)
    Integer getUniqueCorrectAnswers(Integer id);

    /*@Query("select count(distinct a.answerPK.question_id) from answers where a.answerPK.user_id = :id")
    TopicsStatsDTO[] getTopicsStats(Integer id);*/
}