package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.id.MunicipalityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MunicipalityRepository extends JpaRepository<MunicipalityEntity, MunicipalityId> {
    boolean existsByYearAndMunicipalityId(int year, String municipalityId);
}
