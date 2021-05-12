package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.List;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface QuestionDetailsRepository extends JpaRepository<QuestionDetails, Integer> {

    @Query(value = "SELECT * FROM {h-schema}question_details q WHERE q.question_type = 'multiple_choice'", nativeQuery = true)
    List<MultipleChoiceQuestion> findMultipleChoiceQuestionDetails();

    @Query(value = "SELECT * FROM {h-schema}question_details q WHERE q.question_type = 'code_fill_in'", nativeQuery = true)
    List<CodeFillInQuestion> findCodeFillInQuestionDetails();

    @Query(value = "SELECT * FROM {h-schema}question_details q WHERE q.question_type = 'code_order'", nativeQuery = true)
    List<CodeOrderQuestion> findCodeOrderQuestionDetails();
}