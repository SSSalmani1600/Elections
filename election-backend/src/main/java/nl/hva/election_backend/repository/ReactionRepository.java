package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {
    
    @Query("SELECT r FROM ReactionEntity r WHERE r.discussion.id = :discussionId ORDER BY r.createdAt ASC")
    List<ReactionEntity> findByDiscussionId(@Param("discussionId") String discussionId);
    
    long countByDiscussionId(String discussionId);
}

