package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.ElectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<ElectionEntity, Integer> {
    List<ElectionEntity> getAllByYear(int year);
}

