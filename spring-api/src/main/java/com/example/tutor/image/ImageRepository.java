package com.example.tutor.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("select o from Image o where o.question.id = :questionId")
    Optional<Image> findById(Integer questionId);

}