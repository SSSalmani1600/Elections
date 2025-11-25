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
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("APPROVED");
        reactionRepository.save(reaction);
    }

    public void rejectReaction(Long id) {
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("REJECTED");
        reactionRepository.save(reaction);
    }

    public void flagReaction(Long id, String reason) {
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("FLAGGED");
        reaction.setFlaggedReason(reason);
        reactionRepository.save(reaction);
    }
}
