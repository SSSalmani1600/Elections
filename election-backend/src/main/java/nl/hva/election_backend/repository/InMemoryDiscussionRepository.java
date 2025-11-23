package nl.hva.election_backend.repository;

import nl.hva.election_backend.model.Discussion;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.hva.election_backend.dto.ReactionDto;
import java.util.HashMap;
import java.util.Map;

/**
 * Simpele in-memory opslag van discussies.
 * Later kun je dit vervangen door een echte database.
 */
public class InMemoryDiscussionRepository {

    private final List<Discussion> discussions = new ArrayList<>();
    private final Map<String, List<ReactionDto>> reactionsByDiscussion = new HashMap<>();


    public InMemoryDiscussionRepository() {
        // Gebruik één referentietijdstip zodat lastActivityAt > createdAt
        Instant now = Instant.now();

        // voorbeeld discussies
        discussions.add(
                Discussion.create("Verkiezingsdebat 2025", "Nabil",
                                "Wat vonden jullie van het debat gisteravond?")
                        .withActivity(now.plusSeconds(3600), 5)
        );
        discussions.add(
                Discussion.create("Stemrecht voor jongeren", "Daan",
                                "Moeten 16-jarigen mogen stemmen?")
                        .withActivity(now.plusSeconds(7200), 8)
        );
        discussions.add(
                Discussion.create("Toekomst van de EU", "Sara",
                                "Wat denken jullie van uitbreiding van de EU?")
                        .withActivity(now.plusSeconds(10800), 2)
        );
    }

    /** Alle discussies in de huidige volgorde (nieuwste bovenaan toegevoegd) */
    public List<Discussion> findAll() {
        return new ArrayList<>(discussions);
    }

    /** Zoek 1 discussie op id */
    public Optional<Discussion> findById(String id) {
        return discussions.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    /** Zoek een detail view van 1 discussie (met reacties) */
    public Optional<nl.hva.election_backend.dto.DiscussionDetailDto> findDetailById(String id) {
        return findById(id)
                .map(d -> {
                    List<ReactionDto> reactions =
                            reactionsByDiscussion.getOrDefault(d.getId(), List.of());
                    return new nl.hva.election_backend.dto.DiscussionDetailDto(
                            d.getId(),
                            d.getTitle(),
                            d.getAuthor(),
                            d.getBody(),
                            d.getCreatedAt(),
                            d.getLastActivityAt(),
                            d.getReactionsCount(),
                            reactions
                    );
                });
    }

    /** Nieuwe discussie opslaan — bovenaan zetten */
    public void save(Discussion discussion) {
        discussions.add(0, discussion);
    }

    /** Reactie toevoegen bij discussie */
    public ReactionDto addReaction(String discussionId, String author, String message, Instant createdAt) {
        Discussion discussion = findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found: " + discussionId));

        ReactionDto reaction = new ReactionDto(author, message, createdAt);

        reactionsByDiscussion
                .computeIfAbsent(discussionId, id -> new ArrayList<>())
                .add(reaction);

        // activiteit + teller updaten
        Discussion updated = discussion.withActivity(
                createdAt.isAfter(discussion.getLastActivityAt()) ? createdAt : discussion.getLastActivityAt(),
                discussion.getReactionsCount() + 1
        );

        // oude discussie vervangen
        for (int i = 0; i < discussions.size(); i++) {
            if (discussions.get(i).getId().equals(discussionId)) {
                discussions.set(i, updated);
                break;
            }
        }

        return reaction;
    }
}
