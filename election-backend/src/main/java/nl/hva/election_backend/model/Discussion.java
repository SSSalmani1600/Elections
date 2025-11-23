package nl.hva.election_backend.model;

import java.time.Instant;

public class Discussion {

    private String id;          // id als string (wordt long in DB)
    private String title;
    private String body;
    private String category;
    private Long userId;        // <-- BELANGRIJK
    private String author;      // username (alleen voor frontend)
    private Instant createdAt;
    private Instant lastActivityAt;
    private int reactionsCount;

    public Discussion() {}

    /** Nieuwe discussie aanmaken vanuit controller */
    public static Discussion create(String title, String body, String category, Long userId) {
        Discussion d = new Discussion();
        d.title = title;
        d.body = body;
        d.category = category != null ? category : "algemeen";
        d.userId = userId;
        d.createdAt = Instant.now();
        d.lastActivityAt = Instant.now();
        d.reactionsCount = 0;
        return d;
    }

    /** Mapping vanuit Entity */
    public static Discussion fromEntity(
            String id,
            String title,
            String author,
            String body,
            String category,
            Instant createdAt,
            Instant lastActivityAt,
            int reactionsCount
    ) {
        Discussion d = new Discussion();
        d.id = id;
        d.title = title;
        d.author = author;
        d.body = body;
        d.category = category;
        d.createdAt = createdAt;
        d.lastActivityAt = lastActivityAt;
        d.reactionsCount = reactionsCount;
        return d;
    }

    // --------- GETTERS ----------

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCategory() { return category; }
    public Long getUserId() { return userId; }     // <-- nu bestaat dit
    public String getAuthor() { return author; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    // --------- SETTERS (nodig voor service) ----------

    public void setId(String id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public void setCategory(String category) { this.category = category; }
    public void setReactionsCount(int count) { this.reactionsCount = count; }
}
