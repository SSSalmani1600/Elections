// Regelt logica tussen controller en repo.
// Zegt tegen repo wat hij moet halen
package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.model.Discussion;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service // maakt duidelijk dat dit logica laag is
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;

    public DiscussionService(DiscussionRepository discussionRepository, ReactionRepository reactionRepository) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
    }

    // haalt lijst met discussies op
    public List<Discussion> list() {
        List<DiscussionEntity> entities = discussionRepository.findAllOrderByLastActivityDesc();
        return entities.stream()
                .map(this::toDiscussion)
                .collect(Collectors.toList());
    }

    // haalt detail van 1 discussie op (met reacties)
    @Transactional(readOnly = true)
    public DiscussionDetailDto getDetailById(String id) {
        DiscussionEntity entity = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));
        
        List<ReactionEntity> reactions = reactionRepository.findByDiscussionId(id);
        List<ReactionDto> reactionDtos = reactions.stream()
                .map(r -> new ReactionDto(r.getAuthor(), r.getMessage(), r.getCreatedAt()))
                .collect(Collectors.toList());
        
        return new DiscussionDetailDto(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getLastActivityAt(),
                entity.getReactionsCount(),
                reactionDtos
        );
    }

    // nieuw topic opslaan
    @Transactional
    public void save(Discussion d) {
        DiscussionEntity entity = new DiscussionEntity(
                d.getId(),
                d.getTitle(),
                d.getAuthor(),
                d.getBody(),
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount()
        );
        discussionRepository.save(entity);
    }

    /** Nieuwe reactie toevoegen aan bestaande discussie */
    @Transactional
    public ReactionDto addReaction(String discussionId, String author, String message) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found: " + discussionId));
        
        Instant now = Instant.now();
        ReactionEntity reaction = new ReactionEntity(discussion, author, message, now);
        reactionRepository.save(reaction);
        
        // Update discussion activity
        Instant newLastActivity = now.isAfter(discussion.getLastActivityAt()) 
                ? now 
                : discussion.getLastActivityAt();
        long newReactionsCount = reactionRepository.countByDiscussionId(discussionId);
        
        discussion.setLastActivityAt(newLastActivity);
        discussion.setReactionsCount((int) newReactionsCount);
        discussionRepository.save(discussion);
        
        return new ReactionDto(author, message, now);
    }

    // Helper method to convert entity to model
    private Discussion toDiscussion(DiscussionEntity entity) {
        return Discussion.fromEntity(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getLastActivityAt(),
                entity.getReactionsCount()
        );
    }
}
