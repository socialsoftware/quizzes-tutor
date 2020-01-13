package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;

import java.util.Arrays;
import java.util.List;

@Repository
@Transactional
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    @Query(value = "SELECT * FROM assessments a INNER JOIN course_executions ce ON ce.id = a.course_execution_id WHERE ce.acronym = :acronym AND ce.academic_term = :academicTerm", nativeQuery = true)
    List<Assessment> findExecutionCourseAssessments(String acronym, String academicTerm);
}