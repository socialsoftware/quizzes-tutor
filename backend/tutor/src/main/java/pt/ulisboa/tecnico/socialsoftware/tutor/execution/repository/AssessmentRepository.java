package pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    @Query(value = "SELECT * FROM assessments a, course_executions c WHERE c.id = :courseExecutionId AND c.id = a.course_execution_id ORDER BY a.id", nativeQuery = true)
    List<Assessment>  findByExecutionCourseId(int courseExecutionId);

    @Query(value = "SELECT a.course_execution_id FROM assessments a WHERE a.id = :id", nativeQuery = true)
    Optional<Integer> findCourseExecutionIdById(int id);
}