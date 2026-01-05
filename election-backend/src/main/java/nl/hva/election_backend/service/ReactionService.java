package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.exception.ResourceNotFoundException;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final ModerationService moderationService;

    public ReactionService(ReactionRepository reactionRepository,
                           DiscussionRepository discussionRepository,
                           UserRepository userRepository,
                           ModerationService moderationService) {
        this.reactionRepository = reactionRepository;
        this.discussionRepository = discussionRepository;
        this.userRepository = userRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 📌 AI wordt ALLEEN hier aangeroepen
        ModerationResult moderation = moderationService.moderateText(message);

        ReactionEntity reaction = new ReactionEntity();
        reaction.setDiscussion(discussion);
        reaction.setUser(user);
        reaction.setCreatedAt(Instant.now());
        reaction.setMessage(moderation.getModeratedText());

        // Status exact overnemen
        reaction.setModerationStatus(moderation.getModerationStatus());

        if (moderation.isFlagged() || moderation.isBlocked()) {
            reaction.setFlaggedReason(String.join(", ", moderation.getWarnings()));
        }

        ReactionEntity saved = reactionRepository.save(reaction);

        // Update de discussie: verhoog reactie teller en update laatste activiteit
        discussion.setReactionsCount(discussion.getReactionsCount() + 1);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        return saved;
    }

    /**
     * Bewerkt een reactie als de gebruiker de eigenaar is
     */
    public ReactionEntity updateReaction(Long reactionId, Long userId, String newMessage) {
        // Zoek de reactie in de database
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Reactie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de reactie
        if (reaction.getUser() == null || !reaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen reacties bewerken");
        }

        // Update de reactie met de gemodereerde tekst
        reaction.setMessage(newMessage);
        
        return reactionRepository.save(reaction);
    }

    /**
     * Verwijdert een reactie als de gebruiker de eigenaar is
     */
    public void deleteReaction(Long reactionId, Long userId) {
        // Zoek de reactie in de database
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Reactie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de reactie
        if (reaction.getUser() == null || !reaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen reacties verwijderen");
        }

        // Haal de discussie op om de teller te updaten
        DiscussionEntity discussion = reaction.getDiscussion();

        // Verwijder de reactie
        reactionRepository.delete(reaction);

        // Update de reactie teller van de discussie
        discussion.setReactionsCount(Math.max(0, discussion.getReactionsCount() - 1));
        discussionRepository.save(discussion);
    }

}
