package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.PartyViewpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyViewpointRepository extends JpaRepository<PartyViewpointEntity, Long> {
}