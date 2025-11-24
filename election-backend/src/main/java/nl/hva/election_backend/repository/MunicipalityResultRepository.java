package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.MunicipalityResultEntity;
import nl.hva.election_backend.entity.id.MunicipalityResultId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MunicipalityResultRepository extends JpaRepository<MunicipalityResultEntity, MunicipalityResultId> {
    boolean existsByYearAndMunicipalityIdAndPartyId(int year, String municipalityId, String partyId);
}
