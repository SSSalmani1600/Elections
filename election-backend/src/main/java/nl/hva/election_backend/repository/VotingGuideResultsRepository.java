package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.VotingGuideResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VotingGuideResultsRepository extends JpaRepository<VotingGuideResultEntity, Long> {
    boolean existsByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
