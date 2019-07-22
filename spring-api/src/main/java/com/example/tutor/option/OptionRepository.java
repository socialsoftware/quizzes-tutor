package com.example.tutor.option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, OptionKey> {

    @Query(value = "select o from options o where o.question_id = :questionId", nativeQuery = true)
    List<Option> findAllById(Integer questionId);

    @Query(value = "select o from options o where o.question_id = :questionId and o.option = :option", nativeQuery = true)
    Optional<Option> findById(Integer questionId, Integer option);

}