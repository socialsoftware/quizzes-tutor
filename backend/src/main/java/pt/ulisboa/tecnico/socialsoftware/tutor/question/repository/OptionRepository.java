package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.Set;

@Repository
@Transactional
public interface OptionRepository extends JpaRepository<Option, Integer> {
    @Query(value = "SELECT option.questionDetails.question FROM Option option WHERE option.questionDetails.question.course.id = :courseId AND option.content LIKE %:content%")
    Set<Question> findQuestionsByOptionContent(int courseId, String content);
}