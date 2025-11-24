package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.id.ConstituencyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConstituencyRepository extends JpaRepository<ConstituencyEntity, ConstituencyId> {
    Optional<ConstituencyEntity> findByNameAndYear(String name, int year);
}
