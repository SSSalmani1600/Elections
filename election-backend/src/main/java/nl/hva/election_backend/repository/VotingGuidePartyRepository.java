package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.VotingGuidePartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingGuidePartyRepository extends JpaRepository<VotingGuidePartyEntity, Long> {
}
