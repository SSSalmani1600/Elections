package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.DiscussionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionEntity, Long> {

    List<DiscussionEntity> findAllByOrderByLastActivityAtDesc();
}
