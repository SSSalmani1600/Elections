package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
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

    // ---------------- LIST ALL ----------------
    @Transactional(readOnly = true)
    public List<DiscussionListItemDto> list() {

        List<DiscussionEntity> entities =
                discussionRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "lastActivityAt")
                );

        return entities.stream()
                .map(e -> new DiscussionListItemDto(
                        e.getId().toString(),          // <-- RECORD REQUIRES STRING
                        e.getTitle(),
                        getUserName(e.getUserId()),
                        e.getLastActivityAt(),
                        e.getReactionsCount()
                ))
                .collect(Collectors.toList());
    }

    // ---------------- DETAIL ----------------
    @Transactional(readOnly = true)
    public DiscussionDetailDto getDetailById(Long id) {

        DiscussionEntity entity = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        String author = getUserName(entity.getUserId());

        List<ReactionDto> reactionDtos = reactionRepository.findByDiscussionId(id)
                .stream()
                .map(r -> new ReactionDto(
                        getUserName(r.getUserId()),
                        r.getMessage(),
                        r.getCreatedAt()
                ))
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

    // ---------------- CREATE NEW DISCUSSION ----------------
    @Transactional
    public Long createDiscussion(String title, String body, String category, Long userId) {

        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Instant now = Instant.now();

        DiscussionEntity entity = new DiscussionEntity(
                null,           // auto-gen id
                title,
                body,
                category != null ? category : "algemeen",
                userId,
                now,
                now,
                0
        );

        DiscussionEntity saved = discussionRepository.save(entity);
        return saved.getId();
    }

    // ---------------- ADD REACTION ----------------
    @Transactional
    public ReactionDto addReaction(Long discussionId, Long userId, String message) {

        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Instant now = Instant.now();

        ReactionEntity reaction = new ReactionEntity(
                discussion,
                userId,
                message,
                now
        );

        reactionRepository.save(reaction);

        long count = reactionRepository.countByDiscussionId(discussionId);

        discussion.setLastActivityAt(now);
        discussion.setReactionsCount((int) count);
        discussionRepository.save(discussion);

        return new ReactionDto(
                user.getUsername(),
                message,
                now
        );
    }

    // ---------------- HELPERS ----------------
    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown");
    }
}
