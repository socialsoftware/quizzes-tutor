package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import java.util.List;

@Repository
@Transactional
public interface QuestionDetailsRepository extends JpaRepository<QuestionDetails, Integer> {

    @Query(value = "SELECT * FROM question_details q WHERE q.question_type = 'multiple_choice'", nativeQuery = true)
    List<MultipleChoiceQuestion> findMultipleChoiceQuestionDetails();

    @Query(value = "SELECT * FROM question_details q WHERE q.question_type = 'code_fill_in'", nativeQuery = true)
    List<CodeFillInQuestion> findCodeFillInQuestionDetails();

    @Query(value = "SELECT * FROM question_details q WHERE q.question_type = 'code_order'", nativeQuery = true)
    List<CodeOrderQuestion> findCodeOrderQuestionDetails();
}