package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration;

import java.util.List;

@Repository
@Transactional
public interface QuestionGenerationRepository extends JpaRepository<QuestionGeneration, Integer> {
    @Query(value = "select g.* from question_generations g where g.course_execution_id = :courseExecutionId order by g.id", nativeQuery = true)
    List<QuestionGeneration> findByCourseExecution(@Param("courseExecutionId") Integer courseExecutionId);

    @Query(value = "select g.* from question_generations g where g.question_id = :questionId", nativeQuery = true)
    QuestionGeneration findByQuestionId(@Param("questionId") Integer questionId);
}
