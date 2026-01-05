package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ReactionDto;

import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

// Service laag: bevat de business logica voor discussies
// Dit is de laag tussen de controller en de database
@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    // Constructor: Spring injecteert automatisch de repositories
    public DiscussionService(
            DiscussionRepository discussionRepository,
            ReactionRepository reactionRepository,
            UserRepository userRepository
    ) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
    }

    // Haalt alle discussies op en zet ze om naar DTO's voor de lijstweergave
    public List<DiscussionListItemDto> list() {
        return list(0, Integer.MAX_VALUE);
    }

    public List<DiscussionListItemDto> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastActivityAt").descending());
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
        // Zoek de discussie in de database
        DiscussionEntity d = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        // Haal alle reacties op voor deze discussie
        List<ReactionEntity> reactions =
                reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(id)
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

    // Maakt een nieuwe discussie aan in de database
    public Long createDiscussion(String title, String content, String category, Long userId) {
        // Zoek de gebruiker op
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Maak een nieuwe database entiteit aan
        DiscussionEntity entity = new DiscussionEntity();
        entity.setTitle(title);
        entity.setBody(content);
        entity.setCategory(category);
        entity.setUser(user);
        entity.setCreatedAt(Instant.now());
        entity.setLastActivityAt(Instant.now());
        entity.setReactionsCount(0); // Nieuwe discussie heeft nog geen reacties

        // Sla op in database en geef het nieuwe ID terug
        DiscussionEntity saved = discussionRepository.save(entity);
        return saved.getId();
    }

    // Bewerkt een discussie als de gebruiker de eigenaar is
    public DiscussionDetailDto updateDiscussion(Long discussionId, Long userId, String newTitle, String newBody) {
        // Zoek de discussie in de database
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de discussie
        if (discussion.getUser() == null || !discussion.getUser().getId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen discussies bewerken");
        }

        // Update de discussie
        discussion.setTitle(newTitle);
        discussion.setBody(newBody);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        // Geef de bijgewerkte discussie terug
        return getDetailById(discussionId);
    }

    // Verwijdert een discussie als de gebruiker de eigenaar is
    @Transactional
    public void deleteDiscussion(Long discussionId, Long userId) {
        // Zoek de discussie in de database
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de discussie
        if (discussion.getUser() == null || !discussion.getUser().getId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen discussies verwijderen");
        }

        // Verwijder eerst alle reacties van deze discussie
        reactionRepository.deleteAllByDiscussion_Id(discussionId);

        // Verwijder de discussie zelf
        discussionRepository.delete(discussion);
    }

}
