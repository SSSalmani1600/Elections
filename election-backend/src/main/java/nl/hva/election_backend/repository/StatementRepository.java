package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.StatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LinkedHashSet;

public interface StatementRepository extends JpaRepository<StatementEntity, Long> {
    public LinkedHashSet<StatementEntity> findAllByOrderByIdAsc();
}
