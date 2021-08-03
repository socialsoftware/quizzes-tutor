package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.Set;

@Repository
@Transactional
public interface CodeFillInOptionRepository extends JpaRepository<CodeFillInOption, Integer> {
    @Query(value = "SELECT option.codeFillInSpot.questionDetails.question FROM CodeFillInOption option WHERE option.codeFillInSpot.questionDetails.question.course.id = :courseId AND option.content LIKE %:content%")
    Set<Question> findQuestionsByCodeFillInOptionContent(int courseId, String content);
}