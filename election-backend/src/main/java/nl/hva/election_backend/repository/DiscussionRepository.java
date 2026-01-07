package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.DiscussionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository: interface voor database queries op discussies
// Spring genereert automatisch de implementatie
@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionEntity, Long> {

    // Haalt alle discussies op met user data, gesorteerd op laatste activiteit pageable
    @Query(value = """
        SELECT d FROM DiscussionEntity d
        LEFT JOIN FETCH d.user
    """, countQuery = "SELECT count(d) FROM DiscussionEntity d")
    Page<DiscussionEntity> findAllWithUser(Pageable pageable);

    // Haalt alle discussies op met user data, gesorteerd op laatste activiteit
    // LEFT JOIN FETCH zorgt ervoor dat user data in 1 query wordt opgehaald
    @Query("""
        SELECT d FROM DiscussionEntity d
        LEFT JOIN FETCH d.user
        ORDER BY d.lastActivityAt DESC
    """)
    List<DiscussionEntity> findAllWithUserOrdered();

    // Haalt alle discussies op, gesorteerd op laatste activiteit (zonder user data)
    List<DiscussionEntity> findAllByOrderByLastActivityAtDesc();

    // Haalt alle discussies op van een specifieke gebruiker, gesorteerd op aanmaakdatum
    List<DiscussionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
