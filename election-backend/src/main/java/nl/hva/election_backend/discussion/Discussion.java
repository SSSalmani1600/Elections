package nl.hva.election_backend.discussion;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Eenvoudig domeinobject voor een discussie.
 * Geen JPA, puur in-memory voor nu.
 */
public final class Discussion {
    private final String id;
    private final String title;
    private final String author;
    private final String body;
    private final Instant createdAt;
    private final Instant lastActivityAt;
    private final int reactionsCount;

    private Discussion(
            String id,
            String title,
            String author,
            String body,
            Instant createdAt,
            Instant lastActivityAt,
            int reactionsCount
    ) {
        this.id = Objects.requireNonNull(id);
        this.title = validateNonBlank(title, "title");
        this.author = validateNonBlank(author, "author");
        this.body = validateNonBlank(body, "body");
        this.createdAt = Objects.requireNonNull(createdAt);
        this.lastActivityAt = Objects.requireNonNull(lastActivityAt);
        if (lastActivityAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("lastActivityAt < createdAt");
        }
        this.reactionsCount = Math.max(0, reactionsCount);
    }

    private static String validateNonBlank(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is leeg");
        return s.trim();
    }

    /** Factory voor nieuwe discussies (nu = created & lastActivity). */
    public static Discussion create(String title, String author, String body) {
        Instant now = Instant.now();
        return new Discussion(UUID.randomUUID().toString(), title, author, body, now, now, 0);
    }

    /** Nieuwe instantie met geüpdatete activiteit/reacties. */
    public Discussion withActivity(Instant lastActivityAt, int reactionsCount) {
        return new Discussion(
                this.id,
                this.title,
                this.author,
                this.body,
                this.createdAt,
                Objects.requireNonNull(lastActivityAt),
                Math.max(0, reactionsCount)
        );
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getBody() { return body; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discussion d)) return false;
        return id.equals(d.id);
    }
    @Override public int hashCode() { return id.hashCode(); }

    @Override public String toString() {
        return "Discussion{id='%s', title='%s'}".formatted(id, title);
    }
}
