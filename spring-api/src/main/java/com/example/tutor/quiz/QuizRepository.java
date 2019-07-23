package com.example.tutor.quiz;


import com.example.tutor.stats.AnsweredQuizDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @Query("select q from Quiz q where q.generated_by = :id")
    QuizDTO getQuizzes(Integer id);
}