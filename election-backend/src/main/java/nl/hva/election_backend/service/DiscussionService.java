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
                reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(id);

        return new DiscussionDetailDto(
                d.getId().toString(),
                d.getTitle(),

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

    public ReactionDto addReaction(Long discussionId, Long userId, String message) {

        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow();

        // 🔥 Scheldwoordenlijst
        List<String> bannedWords = List.of(
                "kanker", "kut", "tering", "hoer", "fuck", "sukkel", "idioot", "mongool"
        );

        String lower = message.toLowerCase();
        for (String word : bannedWords) {
            if (lower.contains(word)) {
                // ❌ NIET OPSLAAN – direct blokkeren
                throw new IllegalArgumentException("Reactie afgekeurd vanwege ongepast taalgebruik.");
            }
        }

        // ✔ Geen scheldwoorden → wel opslaan als PENDING
        ReactionEntity r = new ReactionEntity();
        r.setDiscussion(discussion);
        r.setUserId(userId);
        r.setMessage(message);
        r.setCreatedAt(Instant.now());
        r.setModerationStatus("PENDING");

        ReactionEntity saved = reactionRepository.save(r);

        discussion.setReactionsCount(discussion.getReactionsCount() + 1);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        return new ReactionDto(
                saved.getId(),
                userRepository.findById(saved.getUserId())
                        .map(u -> u.getUsername())
                        .orElse("Onbekend"),
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }

}
