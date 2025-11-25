package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModerationService {

    private final ReactionRepository reactionRepository;

    public ModerationService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public List<ReactionEntity> getPendingReactions() {
        return reactionRepository.findByModerationStatus("PENDING");
    }

    public List<ReactionEntity> getFlaggedReactions() {
        return reactionRepository.findByModerationStatus("FLAGGED");
    }

    public void approveReaction(Long id) {
        ReactionEntity r = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        r.setModerationStatus("APPROVED");
        reactionRepository.save(r);
    }

    public void rejectReaction(Long id) {
        ReactionEntity r = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        r.setModerationStatus("REJECTED");
        reactionRepository.save(r);
    }

    public void flagReaction(Long id, String reason) {
        ReactionEntity r = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        r.setModerationStatus("FLAGGED");
        r.setFlaggedReason(reason);
        reactionRepository.save(r);
    }
}
