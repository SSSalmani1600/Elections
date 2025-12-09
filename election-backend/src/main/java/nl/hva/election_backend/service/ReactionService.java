package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }


    public ReactionEntity addReaction(Long discussionId, Long userId, String moderatedMessage) {

        ReactionEntity reaction = new ReactionEntity();
        reaction.setDiscussionId(discussionId.intValue());
        reaction.setUserId(userId);
        reaction.setCreatedAt(Instant.now());


        reaction.setMessage(moderatedMessage);


        reaction.setModerationStatus("APPROVED");

        return reactionRepository.save(reaction);
    }
}
