package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    List<ReactionEntity> findAllByDiscussionIdOrderByCreatedAtAsc(Long discussionId);

    long countByDiscussionId(Long discussionId);
}
