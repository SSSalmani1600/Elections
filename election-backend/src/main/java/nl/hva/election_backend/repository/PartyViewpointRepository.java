package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.PartyViewpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyViewpointRepository extends JpaRepository<PartyViewpointEntity, Long> {
}
