package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingGuideAnswerRepository extends JpaRepository<VotingGuideAnswerEntity, Long> {
    boolean existsByUserId(Long id);

    void deleteAllByUserId(Long id);
}
