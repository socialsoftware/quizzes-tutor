package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.Set;

@Repository
@Transactional
public interface CodeOrderSlotRepository extends JpaRepository<CodeOrderSlot, Integer> {
    @Query(value = "SELECT option.questionDetails.question FROM CodeOrderSlot option WHERE option.questionDetails.question.course.id = :courseId AND option.content LIKE %:content%")
    Set<Question> findQuestionsByCodeOrderSlotContent(int courseId, String content);
}