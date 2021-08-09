package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.Set;

@Repository
@Transactional
public interface CodeFillInQuestionRepository extends JpaRepository<CodeFillInQuestion, Integer> {
    @Query(value = "SELECT questionDetails.question FROM CodeFillInQuestion questionDetails WHERE questionDetails.question.course.id = :courseId AND questionDetails.code LIKE %:content%")
    Set<Question> findQuestionsByCodeFillInQuestionContent(int courseId, String content);
}