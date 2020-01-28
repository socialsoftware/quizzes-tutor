package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

@Repository
@Transactional
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer> {
}

