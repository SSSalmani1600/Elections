package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.id.MunicipalityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MunicipalityRepository extends JpaRepository<MunicipalityEntity, MunicipalityId> {
    boolean existsByYearAndMunicipalityId(int year, String municipalityId);

    @Query("SELECT m FROM MunicipalityEntity m " +
            "LEFT JOIN FETCH m.results r " +
            "LEFT JOIN FETCH r.party " +
            "WHERE m.year = :year AND m.name = :name")
    Optional<MunicipalityEntity> findWithResultsByYearsAndName(Integer year, String name);
}
