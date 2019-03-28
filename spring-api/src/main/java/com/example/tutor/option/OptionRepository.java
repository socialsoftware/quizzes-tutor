package com.example.tutor.option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, OptionPK> {

    @Query("select o from Option o where o.optionPK.question.id = :questionID")
    List<Option> findByQuestionID(Integer questionID);

    @Query("select o from Option o where o.optionPK.question.id = :questionID and o.optionPK.option = :option")
    Option findById(Integer questionID, Integer option);

}