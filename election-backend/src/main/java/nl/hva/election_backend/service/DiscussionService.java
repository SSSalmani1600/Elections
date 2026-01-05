package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.CreateDiscussionRequest;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.dto.UpdateDiscussionRequest;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.exception.ForbiddenException;
import nl.hva.election_backend.exception.ResourceNotFoundException;
import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Service laag: bevat de business logica voor discussies
// Dit is de laag tussen de controller en de database
@Service
public class DiscussionService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final ModerationService moderationService;

    // Constructor: Spring injecteert automatisch de repositories
    public DiscussionService(
            DiscussionRepository discussionRepository,
            ReactionRepository reactionRepository,
            UserRepository userRepository,
            ModerationService moderationService
    ) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.moderationService = moderationService;
    }

    // Haalt alle discussies op en zet ze om naar DTO's voor de lijstweergave
    public List<DiscussionListItemDto> list() {
        return list(0, DEFAULT_PAGE_SIZE);
    }

    public List<DiscussionListItemDto> list(int page, int size) {
        int safeSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by("lastActivityAt").descending());
        return discussionRepository.findAllWithUser(pageable)
                .stream()
                .map(entity -> new DiscussionListItemDto(
                        entity.getId().toString(),
                        entity.getTitle(),
                        entity.getUser() != null ? entity.getUser().getUsername() : "Onbekend",
                        entity.getLastActivityAt(),
                        entity.getReactionsCount()
                ))
                .collect(Collectors.toList());
    }

    // Haalt 1 specifieke discussie op met alle details en reacties
    public DiscussionDetailDto getDetailById(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id is verplicht");
        // Zoek de discussie in de database
        DiscussionEntity d = discussionRepository.findById(requiredId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        // Haal alle reacties op voor deze discussie
        List<ReactionEntity> reactions =
                reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(requiredId)
                        .stream()
                        // Filter: toon alleen goedgekeurde of pending reacties
                        .filter(r ->
                                r.getModerationStatus().equals("APPROVED")
                                        || r.getModerationStatus().equals("PENDING")
                        )
                        .collect(Collectors.toList());

        // Maak een DTO object met alle discussie details
        return new DiscussionDetailDto(
                d.getId().toString(),
                d.getUser() != null ? d.getUser().getId() : null,  // userId voor eigenaar check in frontend
                d.getTitle(),
                // Zoek de username van de auteur
                d.getUser() != null ? d.getUser().getUsername() : "Onbekend",
                d.getBody(),
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount(),
                // Zet alle reacties om naar DTO's
                reactions.stream()
                        .map(r -> new ReactionDto(
                                r.getId(),
                                r.getUser() != null ? r.getUser().getId() : null,
                                r.getUser() != null ? r.getUser().getUsername() : "Onbekend",
                                r.getMessage(),
                                r.getCreatedAt()
                        ))
                        .collect(Collectors.toList())
        );
    }

    // Maakt een nieuwe discussie aan in de database (incl. moderatie)
    public DiscussionDetailDto createDiscussion(CreateDiscussionRequest request) {
        ModerationResult modTitle = moderationService.moderateText(request.getTitle());
        ModerationResult modBody = moderationService.moderateText(request.getBody());

        if (modTitle.isBlocked() || modBody.isBlocked()) {
            throw new ForbiddenException("Bericht bevat verboden inhoud.");
        }

        Long userId = Objects.requireNonNull(request.getUserId(), "userId is verplicht");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DiscussionEntity entity = new DiscussionEntity();
        entity.setTitle(modTitle.getModeratedText());
        entity.setBody(modBody.getModeratedText());
        entity.setCategory(request.getCategory() != null ? request.getCategory() : "algemeen");
        entity.setUser(user);
        entity.setCreatedAt(Instant.now());
        entity.setLastActivityAt(Instant.now());
        entity.setReactionsCount(0); // Nieuwe discussie heeft nog geen reacties

        DiscussionEntity saved = discussionRepository.save(entity);
        return getDetailById(saved.getId());
    }

    // Bewerkt een discussie als de gebruiker de eigenaar is (incl. moderatie)
    public DiscussionDetailDto updateDiscussion(Long discussionId, UpdateDiscussionRequest request) {
        Long requiredDiscussionId = Objects.requireNonNull(discussionId, "discussionId is verplicht");

        DiscussionEntity discussion = discussionRepository.findById(requiredDiscussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussie niet gevonden"));

        Long userId = Objects.requireNonNull(request.getUserId(), "userId is verplicht");

        if (discussion.getUser() == null || !discussion.getUser().getId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen discussies bewerken");
        }

        ModerationResult modTitle = moderationService.moderateText(request.getTitle());
        ModerationResult modBody = moderationService.moderateText(request.getBody());
        if (modTitle.isBlocked() || modBody.isBlocked()) {
            throw new ForbiddenException("Bericht bevat verboden inhoud.");
        }

        discussion.setTitle(modTitle.getModeratedText());
        discussion.setBody(modBody.getModeratedText());
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        return getDetailById(requiredDiscussionId);
    }

    // Verwijdert een discussie als de gebruiker de eigenaar is
    @Transactional
    public void deleteDiscussion(Long discussionId, Long userId) {
        Long requiredDiscussionId = Objects.requireNonNull(discussionId, "discussionId is verplicht");
        // Zoek de discussie in de database
        DiscussionEntity discussion = discussionRepository.findById(requiredDiscussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussie niet gevonden"));

        Long requiredUserId = Objects.requireNonNull(userId, "userId is verplicht");

        if (discussion.getUser() == null || !discussion.getUser().getId().equals(requiredUserId)) {
            throw new SecurityException("Je kunt alleen je eigen discussies verwijderen");
        }

        // Verwijder eerst alle reacties van deze discussie
        reactionRepository.deleteAllByDiscussion_Id(requiredDiscussionId);

        // Verwijder de discussie zelf
        discussionRepository.delete(discussion);
    }

}
