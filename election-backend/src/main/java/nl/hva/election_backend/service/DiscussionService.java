// Regelt logica tussen controller en repo.
// Zegt tegen repo wat hij moet halen
package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.model.Discussion;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service // maakt duidelijk dat dit logica laag is
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final TestRepository userRepository;

    public DiscussionService(DiscussionRepository discussionRepository, ReactionRepository reactionRepository, TestRepository userRepository) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
    }

    // haalt lijst met discussies op
    @Transactional(readOnly = true)
    public List<Discussion> list() {
        try {
            List<DiscussionEntity> entities = discussionRepository.findAll(
                    Sort.by(Sort.Direction.DESC, "lastActivityAt")
            );
            return entities.stream()
                    .map(this::toDiscussion)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in DiscussionService.list(): " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // haalt detail van 1 discussie op (met reacties)
    @Transactional(readOnly = true)
    public DiscussionDetailDto getDetailById(Long id) {
        DiscussionEntity entity = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        String author = getUserName(entity.getUserId());

        List<ReactionEntity> reactions = reactionRepository.findByDiscussionId(id);
        List<ReactionDto> reactionDtos = reactions.stream()
                .map(r -> {
                    String authorName = getUserName(r.getUserId());
                    return new ReactionDto(authorName, r.getMessage(), r.getCreatedAt());
                })
                .collect(Collectors.toList());

        return new DiscussionDetailDto(
                entity.getId().toString(),
                entity.getTitle(),
                author,
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getLastActivityAt(),
                entity.getReactionsCount(),
                reactionDtos
        );
    }

    // nieuw topic opslaan
    @Transactional
    public Long save(Discussion d, String category) {
        // Try to parse author as userId, if it fails, look up by username
        Long userId;
        try {
            userId = Long.parseLong(d.getAuthor());
        } catch (NumberFormatException e) {
            // If author is not a number, try to find user by username
            userId = userRepository.findAll().stream()
                    .filter(u -> u.getUsername().equals(d.getAuthor()))
                    .findFirst()
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + d.getAuthor()));
        }

        DiscussionEntity entity = new DiscussionEntity(
                d.getId() != null && !d.getId().isEmpty() ? Long.parseLong(d.getId()) : null,
                d.getTitle(),
                d.getBody(),
                category != null ? category : (d.getCategory() != null ? d.getCategory() : "algemeen"),
                userId,
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount()
        );
        DiscussionEntity saved = discussionRepository.save(entity);
        return saved.getId();
    }

    // Overload without category
    @Transactional
    public Long save(Discussion d) {
        return save(d, null);
    }

    /** Nieuwe reactie toevoegen aan bestaande discussie */
    @Transactional
    public ReactionDto addReaction(Long discussionId, Long userId, String message) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found: " + discussionId));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Instant now = Instant.now();
        ReactionEntity reaction = new ReactionEntity(discussion, userId, message, now);
        reactionRepository.save(reaction);

        // Update discussion activity
        Instant newLastActivity = now.isAfter(discussion.getLastActivityAt())
                ? now
                : discussion.getLastActivityAt();
        long newReactionsCount = reactionRepository.countByDiscussionId(discussionId);

        discussion.setLastActivityAt(newLastActivity);
        discussion.setReactionsCount((int) newReactionsCount);
        discussionRepository.save(discussion);

        String authorName = user.getUsername();
        return new ReactionDto(authorName, message, now);
    }

    /** Update een bestaande reactie */
    @Transactional
    public ReactionDto updateReaction(Long reactionId, String message) {
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + reactionId));

        reaction.setMessage(message);
        reactionRepository.save(reaction);

        // Update discussion activity
        DiscussionEntity discussion = reaction.getDiscussion();
        Instant now = Instant.now();
        discussion.setLastActivityAt(now);
        discussionRepository.save(discussion);

        String authorName = getUserName(reaction.getUserId());
        return new ReactionDto(authorName, message, reaction.getCreatedAt());
    }

    /** Verwijder een reactie */
    @Transactional
    public void deleteReaction(Long reactionId) {
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + reactionId));

        DiscussionEntity discussion = reaction.getDiscussion();
        reactionRepository.delete(reaction);

        // Update discussion activity and count
        long newReactionsCount = reactionRepository.countByDiscussionId(discussion.getId());
        discussion.setReactionsCount((int) newReactionsCount);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);
    }

    /** Update een discussie */
    @Transactional
    public DiscussionDetailDto updateDiscussion(Long id, String title, String body, String category) {
        DiscussionEntity entity = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        if (title != null && !title.isBlank()) {
            entity.setTitle(title);
        }
        if (body != null && !body.isBlank()) {
            entity.setBody(body);
        }
        if (category != null) {
            entity.setCategory(category);
        }
        entity.setLastActivityAt(Instant.now());

        discussionRepository.save(entity);

        return getDetailById(id);
    }

    /** Verwijder een discussie */
    @Transactional
    public void deleteDiscussion(Long id) {
        DiscussionEntity entity = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        // Reactions will be deleted automatically due to CASCADE
        discussionRepository.delete(entity);
    }

    // Helper method to convert entity to model
    private Discussion toDiscussion(DiscussionEntity entity) {
        String author = getUserName(entity.getUserId());
        return Discussion.fromEntity(
                entity.getId().toString(),
                entity.getTitle(),
                author,
                entity.getBody(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getLastActivityAt(),
                entity.getReactionsCount()
        );
    }

    // Helper method to get username from userId
    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown");
    }
}
