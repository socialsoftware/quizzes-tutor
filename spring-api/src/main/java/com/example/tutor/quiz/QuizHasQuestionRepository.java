package com.example.tutor.quiz;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizHasQuestionRepository extends JpaRepository<QuizHasQuestion, QuizHasQuestionKey> {

}