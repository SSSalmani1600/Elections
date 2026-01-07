package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.entity.id.PartyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<PartyEntity, PartyId> {
    Optional<PartyEntity> findTopByNameIgnoreCaseOrderByYearDesc(String name);
    Optional<PartyEntity> findTopByPartyIdOrderByYearDesc(String partyId);
    java.util.Optional<PartyEntity> findByNameAndYear(String name, int year);
}
