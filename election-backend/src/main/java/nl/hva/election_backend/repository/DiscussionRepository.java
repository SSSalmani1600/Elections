package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.DiscussionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionEntity, Long> {

    // Originele methode
    List<DiscussionEntity> findAllByOrderByLastActivityAtDesc();

    // Nieuwe methode: User joinen zodat de username beschikbaar is
    @Query("""
        SELECT d FROM DiscussionEntity d
        JOIN FETCH d.user u
        ORDER BY d.lastActivityAt DESC
    """)
    List<DiscussionEntity> findAllWithUserOrdered();
}
