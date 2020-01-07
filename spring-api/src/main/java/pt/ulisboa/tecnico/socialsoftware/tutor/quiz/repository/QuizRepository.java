package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "SELECT * FROM quizzes q INNER JOIN course_executions ce ON ce.id = q.course_execution_id WHERE ce.acronym = :acronym AND ce.academic_term = :academicTerm AND q.type <> 'STUDENT'", nativeQuery = true)
    List<Quiz> findCourseExecutionAvailableTeacherQuizzes(String acronym, String academicTerm);

    @Query(value = "SELECT MAX(number) FROM quizzes", nativeQuery = true)
    Integer getMaxQuizNumber();

    @Query(value = "SELECT * FROM quizzes q WHERE q.number = :number", nativeQuery = true)
    Optional<Quiz> findByNumber(Integer number);
}