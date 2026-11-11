package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.TeacherApiKey;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TeacherApiKeyRepository extends JpaRepository<TeacherApiKey, Integer> {
    List<TeacherApiKey> findByUserId(Integer userId);

    Optional<TeacherApiKey> findByUserIdAndProvider(Integer userId, String provider);
}
