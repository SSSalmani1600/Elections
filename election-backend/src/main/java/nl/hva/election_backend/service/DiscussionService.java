package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ReactionDto;

import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;

import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final TestRepository userRepository;

    public DiscussionService(
            DiscussionRepository discussionRepository,
            ReactionRepository reactionRepository,
            TestRepository userRepository
    ) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
    }

    // ---------------------- LIST -----------------------
    public List<DiscussionListItemDto> list() {

        return discussionRepository.findAllWithUserOrdered()
                .stream()
                .map(entity -> new DiscussionListItemDto(
                        entity.getId().toString(),
                        entity.getTitle(),

                        // Altijd author ophalen via userId → betrouwbaar en 100% correct
                        userRepository.findById(entity.getUserId())
                                .map(u -> u.getUsername())
                                .orElse("Onbekend"),

                        entity.getLastActivityAt(),
                        entity.getReactionsCount()
                ))
                .collect(Collectors.toList());
    }

    // ---------------------- DETAIL ----------------------
    public DiscussionDetailDto getDetailById(Long id) {
        DiscussionEntity d = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        List<ReactionEntity> reactions =
                reactionRepository.findAllByDiscussionIdOrderByCreatedAtAsc(id);

        return new DiscussionDetailDto(
                d.getId().toString(),
                d.getTitle(),

                // Author van discussion
                userRepository.findById(d.getUserId())
                        .map(u -> u.getUsername())
                        .orElse("Onbekend"),

                d.getBody(),
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount(),

                reactions.stream()
                        .map(r -> new ReactionDto(
                                r.getId(),
                                r.getUserId(),
                                userRepository.findById(r.getUserId())
                                        .map(u -> u.getUsername())
                                        .orElse("Onbekend"),
                                r.getMessage(),
                                r.getCreatedAt()
                        ))
                        .collect(Collectors.toList())
        );
    }

    // ---------------------- CREATE DISCUSSION ----------------------
    public Long createDiscussion(String title, String content, String category, Long userId) {

        DiscussionEntity entity = new DiscussionEntity();
        entity.setTitle(title);
        entity.setBody(content);
        entity.setCategory(category);
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now());
        entity.setLastActivityAt(Instant.now());
        entity.setReactionsCount(0);

        DiscussionEntity saved = discussionRepository.save(entity);
        return saved.getId();
    }

    // ---------------------- ADD REACTION ----------------------
    public ReactionDto addReaction(Long discussionId, Long userId, String message) {

        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow();

        ReactionEntity r = new ReactionEntity();
        r.setDiscussion(discussion);
        r.setUserId(userId);
        r.setMessage(message);
        r.setCreatedAt(Instant.now());

        ReactionEntity saved = reactionRepository.save(r);

        discussion.setReactionsCount(discussion.getReactionsCount() + 1);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        return new ReactionDto(
                saved.getId(),
                saved.getUserId(),
                userRepository.findById(saved.getUserId())
                        .map(u -> u.getUsername())
                        .orElse("Onbekend"),
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }

    // ---------------------- UPDATE REACTION ----------------------
    public ReactionDto updateReaction(Long reactionId, Long userId, String newMessage) {
        ReactionEntity r = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reactie niet gevonden"));

        // Check of de gebruiker eigenaar is van de reactie
        if (!r.getUserId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen reacties bewerken");
        }

        r.setMessage(newMessage);
        ReactionEntity saved = reactionRepository.save(r);

        return new ReactionDto(
                saved.getId(),
                saved.getUserId(),
                userRepository.findById(saved.getUserId())
                        .map(u -> u.getUsername())
                        .orElse("Onbekend"),
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }

    // ---------------------- DELETE REACTION ----------------------
    public void deleteReaction(Long reactionId, Long userId) {
        ReactionEntity r = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reactie niet gevonden"));

        // Check of de gebruiker eigenaar is van de reactie
        if (!r.getUserId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen reacties verwijderen");
        }

        // Update de discussion reactionsCount
        DiscussionEntity discussion = r.getDiscussion();
        discussion.setReactionsCount(Math.max(0, discussion.getReactionsCount() - 1));
        discussionRepository.save(discussion);

        reactionRepository.delete(r);
    }
}
