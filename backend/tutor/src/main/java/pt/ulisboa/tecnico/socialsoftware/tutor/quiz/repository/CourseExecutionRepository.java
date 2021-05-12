package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import java.util.Optional;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface CourseExecutionRepository extends JpaRepository<CourseExecution, Integer> {
    @Query(value = "select * from {h-schema}course_executions ce where ce.acronym = :acronym and ce.academic_term = :academicTerm  and ce.type = :type", nativeQuery = true)
    Optional<CourseExecution> findByFields(String acronym, String academicTerm, String type);

    @Modifying
    @Query(value = "delete from {h-schema}users_course_executions uce where uce.course_executions_id = :courseExecutionId", nativeQuery = true)
    void dissociateCourseExecutionUsers(int courseExecutionId);

}