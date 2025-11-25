package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    List<ReactionEntity> findByModerationStatus(String status);

    List<ReactionEntity> findByModerationStatusIn(List<String> statuses);
}
