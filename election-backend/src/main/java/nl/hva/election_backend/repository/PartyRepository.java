package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.entity.id.PartyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<PartyEntity, PartyId> {
    Optional<PartyEntity> findByNameAndYear(String name, int year);
}
