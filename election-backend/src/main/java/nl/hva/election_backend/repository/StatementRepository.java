package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.StatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashSet;
import java.util.List;

public interface StatementRepository extends JpaRepository<StatementEntity, Long> {
    public LinkedHashSet<StatementEntity> findAllByOrderByIdAsc();

    @Query("select distinct s.category from StatementEntity s")
    List<String> findAllCategories();
}
