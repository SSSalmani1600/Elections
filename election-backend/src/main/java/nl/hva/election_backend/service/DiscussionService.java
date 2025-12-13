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

// Service laag: bevat de business logica voor discussies
// Dit is de laag tussen de controller en de database
@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final TestRepository userRepository;

    // Constructor: Spring injecteert automatisch de repositories
    public DiscussionService(
            DiscussionRepository discussionRepository,
            ReactionRepository reactionRepository,
            TestRepository userRepository
    ) {
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
    }

    // Haalt alle discussies op en zet ze om naar DTO's voor de lijstweergave
    public List<DiscussionListItemDto> list() {
        // Haal alle discussies op, gesorteerd op laatste activiteit
        return discussionRepository.findAllWithUserOrdered()
                .stream()
                // Zet elke database entiteit om naar een DTO object
                .map(entity -> new DiscussionListItemDto(
                        entity.getId().toString(),
                        entity.getTitle(),
                        // Zoek de username op basis van userId
                        userRepository.findById(entity.getUserId())
                                .map(u -> u.getUsername())
                                .orElse("Onbekend"),
                        entity.getLastActivityAt(),
                        entity.getReactionsCount()
                ))
                .collect(Collectors.toList());
    }

    // Haalt 1 specifieke discussie op met alle details en reacties
    public DiscussionDetailDto getDetailById(Long id) {
        // Zoek de discussie in de database
        DiscussionEntity d = discussionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));

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
                d.getUserId(),  // userId voor eigenaar check in frontend
                d.getTitle(),
                // Zoek de username van de auteur
                userRepository.findById(d.getUserId())
                        .map(u -> u.getUsername())
                        .orElse("Onbekend"),
                d.getBody(),
                d.getCreatedAt(),
                d.getLastActivityAt(),
                d.getReactionsCount(),
                // Zet alle reacties om naar DTO's
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

    // Maakt een nieuwe discussie aan in de database
    public Long createDiscussion(String title, String content, String category, Long userId) {
        // Maak een nieuwe database entiteit aan
        DiscussionEntity entity = new DiscussionEntity();
        entity.setTitle(title);
        entity.setBody(content);
        entity.setCategory(category);
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now());
        entity.setLastActivityAt(Instant.now());
        entity.setReactionsCount(0); // Nieuwe discussie heeft nog geen reacties

        // Sla op in database en geef het nieuwe ID terug
        DiscussionEntity saved = discussionRepository.save(entity);
        return saved.getId();
    }

    // Voegt een reactie toe aan een discussie (met automatische moderatie)
    public ReactionDto addReaction(Long discussionId, Long userId, String message) {
        // Zoek de discussie waar de reactie bij hoort
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow();

        // Moderatiefilter: check of er scheldwoorden in zitten
        List<String> bannedWords = List.of(
                "kanker", "kut", "tering", "hoer", "fuck", "sukkel", "idioot", "mongool"
        );

        String lower = message.toLowerCase();
        for (String word : bannedWords) {
            if (lower.contains(word)) {
                // Als er een scheldwoord in zit, gooi een foutmelding
                throw new IllegalArgumentException("Reactie afgekeurd vanwege ongepast taalgebruik.");
            }
        }

        // Geen scheldwoorden gevonden: maak de reactie aan
        ReactionEntity r = new ReactionEntity();
        r.setDiscussion(discussion);
        r.setUserId(userId);
        r.setMessage(message);
        r.setCreatedAt(Instant.now());
        r.setModerationStatus("PENDING"); // Nieuwe reacties beginnen als PENDING

        ReactionEntity saved = reactionRepository.save(r);

        // Update de discussie: verhoog reactie teller en update laatste activiteit
        discussion.setReactionsCount(discussion.getReactionsCount() + 1);
        discussion.setLastActivityAt(Instant.now());
        discussionRepository.save(discussion);

        // Geef de opgeslagen reactie terug als DTO
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

    // Bewerkt een reactie als de gebruiker de eigenaar is
    public ReactionDto updateReaction(Long reactionId, Long userId, String newMessage) {
        // Zoek de reactie in de database
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reactie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de reactie
        if (!reaction.getUserId().equals(userId)) {
            throw new SecurityException("Je kunt alleen je eigen reacties bewerken");
        }

        // Moderatiefilter: check of er scheldwoorden in zitten
        List<String> bannedWords = List.of(
                "kanker", "kut", "tering", "hoer", "fuck", "sukkel", "idioot", "mongool"
        );

        String lower = newMessage.toLowerCase();
        for (String word : bannedWords) {
            if (lower.contains(word)) {
                throw new IllegalArgumentException("Reactie afgekeurd vanwege ongepast taalgebruik.");
            }
        }

        // Update de reactie
        reaction.setMessage(newMessage);
        ReactionEntity saved = reactionRepository.save(reaction);

        // Geef de bijgewerkte reactie terug als DTO
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

    // Verwijdert een reactie als de gebruiker de eigenaar is
    public void deleteReaction(Long reactionId, Long userId) {
        // Zoek de reactie in de database
        ReactionEntity reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new IllegalArgumentException("Reactie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de reactie
        if (!reaction.getUserId().equals(userId)) {
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

    // Bewerkt een discussie als de gebruiker de eigenaar is
    public DiscussionDetailDto updateDiscussion(Long discussionId, Long userId, String newTitle, String newBody) {
        // Zoek de discussie in de database
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussie niet gevonden"));

        // Check of de gebruiker de eigenaar is van de discussie
        if (!discussion.getUserId().equals(userId)) {
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

}
