package com.example.tutor.question;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "select count(q) from questions q where q.active = true", nativeQuery = true)
    Integer getTotalUniqueQuestions();

}