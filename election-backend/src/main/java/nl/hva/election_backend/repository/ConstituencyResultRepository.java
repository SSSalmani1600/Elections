package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ConstituencyResultEntity;
import nl.hva.election_backend.entity.id.ConstituencyResultId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstituencyResultRepository extends JpaRepository<ConstituencyResultEntity, ConstituencyResultId> {
    boolean existsByYearAndConstituencyIdAndPartyId(int year, String constituencyId, String partyId);
}
