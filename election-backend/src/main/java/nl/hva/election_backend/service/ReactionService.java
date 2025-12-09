package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final DiscussionRepository discussionRepository;
    private final ModerationService moderationService;

    public ReactionService(ReactionRepository reactionRepository,
                           DiscussionRepository discussionRepository,
                           ModerationService moderationService) {
        this.reactionRepository = reactionRepository;
        this.discussionRepository = discussionRepository;
        this.moderationService = moderationService;
    }

    /**
     * Voeg een reactie toe met AI-moderatie:
     *  - BLOCKED → te ernstig, wordt niet getoond
     *  - FLAGGED → verdacht, admin moet beoordelen
     *  - PENDING → geen problemen, admin moet nog steeds keuren
     */
    public ReactionEntity addReaction(Long discussionId, Long userId, String message) {

        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        // 📌 AI wordt ALLEEN hier aangeroepen
        ModerationResult moderation = moderationService.moderateText(message);

        ReactionEntity reaction = new ReactionEntity();
        reaction.setDiscussion(discussion);
        reaction.setUserId(userId);
        reaction.setCreatedAt(Instant.now());
        reaction.setMessage(moderation.getModeratedText());

        // Status exact overnemen
        reaction.setModerationStatus(moderation.getModerationStatus());

        if (moderation.isFlagged() || moderation.isBlocked()) {
            reaction.setFlaggedReason(String.join(", ", moderation.getWarnings()));
        }

        return reactionRepository.save(reaction);
    }

}
