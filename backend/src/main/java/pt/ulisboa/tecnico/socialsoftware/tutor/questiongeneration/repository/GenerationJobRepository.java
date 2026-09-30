package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob;

import java.util.Optional;

@Repository
@Transactional
public interface GenerationJobRepository extends JpaRepository<GenerationJob, Integer> {
    // Two teachers polling the same job must not both import its questions
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select j from GenerationJob j where j.id = :id")
    Optional<GenerationJob> findByIdForUpdate(@Param("id") Integer id);
}
