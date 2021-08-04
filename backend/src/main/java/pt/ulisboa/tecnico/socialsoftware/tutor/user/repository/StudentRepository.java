package pt.ulisboa.tecnico.socialsoftware.tutor.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @EntityGraph(attributePaths = {"quizAnswers.questionAnswers"})
    Optional<Student> findStudentWithQuizAnswersAndQuestionAnswersById(int userId);
}