package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.VotingGuideResultEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingGuideResultsRepository {
    boolean existsByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    void saveAll(List<VotingGuideResultEntity> listOfResultEntities);
}
