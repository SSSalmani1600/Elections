package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ReactionDto;

import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.model.User;

import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    public DiscussionService(
            DiscussionRepository discussionRepository,
            ReactionRepository reactionRepository,
            UserRepository userRepository
    ) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
    }

    // ---------------------- LIST -----------------------
    public List<DiscussionListItemDto> list() {
        return discussionRepository.findAllByOrderByLastActivityAtDesc()
                .stream()
                .map(entity -> new DiscussionListItemDto(
                        entity.getId().toString(),
                        entity.getTitle(),
                        getUserName(entity.getUserId()),
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
                d.getId().toString(), // ✅ Fix: toString toegevoegd
                d.getTitle(),
                getUserName(d.getUserId()),
                d.getBody(),
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount(),
                reactions.stream()
                        .map(r -> new ReactionDto(
                                r.getId(),
                                r.getMessage(),
                                getUserName(r.getUserId()),
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

        // Update discussion stats
        discussion.setReactionsCount(discussion.getReactionsCount() + 1);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        return new ReactionDto(
                saved.getId(),
                saved.getMessage(),
                getUserName(saved.getUserId()),
                saved.getCreatedAt()
        );
    }

    // ---------------------- USERNAME HELPER ----------------------
    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Onbekend");
    }
}
