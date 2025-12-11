package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotingGuideAnswerRepository extends JpaRepository<VotingGuideAnswerEntity, Long> {
    boolean existsByUserId(Long id);

    void deleteAllByUserId(Long id);

    List<VotingGuideAnswerEntity> findAllByUserId(Long userId);
}
