package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ProfanityService profanityService;

    public ReactionService(ReactionRepository reactionRepository, ProfanityService profanityService) {
        this.reactionRepository = reactionRepository;
        this.profanityService = profanityService;
    }

    public ReactionEntity addReaction(Long discussionId, Long userId, String message) {

        ReactionEntity reaction = new ReactionEntity();
        reaction.setDiscussionId(discussionId.intValue());
        reaction.setUserId((long) userId.intValue());
        reaction.setMessage(message);
        reaction.setCreatedAt(Instant.now());

        // 👉 Automatische moderatie check
        if (profanityService.containsProfanity(message)) {
            reaction.setModerationStatus("FLAGGED");
            reaction.setFlaggedReason("Automatisch gedetecteerd scheldwoord");
        } else {
            reaction.setModerationStatus("PENDING");
        }

        return reactionRepository.save(reaction);
    }
}
