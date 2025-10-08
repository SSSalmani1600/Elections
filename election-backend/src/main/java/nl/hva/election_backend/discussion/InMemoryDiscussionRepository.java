package nl.hva.election_backend.discussion;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Simpele in-memory opslag van discussies.
 * Later kun je dit vervangen door een echte database.
 */
public class InMemoryDiscussionRepository {

    private final List<Discussion> discussions = new ArrayList<>();

    public InMemoryDiscussionRepository() {
        // Gebruik één referentietijdstip zodat lastActivityAt > createdAt
        Instant now = Instant.now();

        // Seed met wat voorbeeld discussies
        discussions.add(
                Discussion.create("Verkiezingsdebat 2025", "Nabil",
                                "Wat vonden jullie van het debat gisteravond?")
                        .withActivity(now.plusSeconds(3600), 5) // 1 uur later
        );
        discussions.add(
                Discussion.create("Stemrecht voor jongeren", "Daan",
                                "Moeten 16-jarigen mogen stemmen?")
                        .withActivity(now.plusSeconds(7200), 8) // 2 uur later
        );
        discussions.add(
                Discussion.create("Toekomst van de EU", "Sara",
                                "Wat denken jullie van uitbreiding van de EU?")
                        .withActivity(now.plusSeconds(10800), 2) // 3 uur later
        );
    }

    /** Alle discussies, gesorteerd op laatste activiteit (nieuwste eerst) */
    public List<Discussion> findAll() {
        return discussions.stream()
                .sorted(Comparator.comparing(Discussion::getLastActivityAt).reversed())
                .toList();
    }

    /** Zoek 1 discussie op id */
    public Optional<Discussion> findById(String id) {
        return discussions.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }
}
