package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    // Jouw methods
    List<ReactionEntity> findByModerationStatus(String status);
    List<ReactionEntity> findByModerationStatusIn(List<String> statuses);

    // Methods uit main
    List<ReactionEntity> findAllByDiscussionIdOrderByCreatedAtAsc(int discussionId);
    long countByDiscussionId(int discussionId);
}
