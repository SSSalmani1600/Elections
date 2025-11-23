package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.PartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<PartyEntity, Long> {
    Optional<PartyEntity> findByNameAndYear(String name, int year);
}
