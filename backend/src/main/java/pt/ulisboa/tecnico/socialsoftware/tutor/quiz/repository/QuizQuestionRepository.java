package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
}
