package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

@Repository
@Transactional
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
}
