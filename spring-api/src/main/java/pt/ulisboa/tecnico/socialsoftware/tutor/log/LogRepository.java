package pt.ulisboa.tecnico.socialsoftware.tutor.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LogRepository extends JpaRepository<Log, Integer> {
}

