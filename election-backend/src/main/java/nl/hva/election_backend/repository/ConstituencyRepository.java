package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.id.ConstituencyId;
import nl.hva.election_backend.model.Constituency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConstituencyRepository extends JpaRepository<ConstituencyEntity, ConstituencyId> {
    Optional<ConstituencyEntity> findByNameAndYear(String name, int year);

    List<ConstituencyEntity> findAllByYear(int year);
}
