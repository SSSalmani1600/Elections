package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.DiscussionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionEntity, Long> {

    @Query("""
        SELECT d FROM DiscussionEntity d
        LEFT JOIN FETCH d.user
        ORDER BY d.lastActivityAt DESC
    """)
    List<DiscussionEntity> findAllWithUserOrdered();

    List<DiscussionEntity> findAllByOrderByLastActivityAtDesc();

    // Haal alle discussies op van een specifieke gebruiker
    List<DiscussionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
