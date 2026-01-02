package nl.hva.election_backend.model;

import java.time.Instant;

// Model class voor discussie (geen database entiteit - die is DiscussionEntity)
public class Discussion {

    private String id;
    private String title;
    private String body;
    private String category;
    private Long userId;
    private String author;
    private Instant createdAt;
    private Instant lastActivityAt;
    private int reactionsCount;

    public Discussion() {}

    // maak nieuwe discussie
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

    // maak van entity
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

    // getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCategory() { return category; }
    public Long getUserId() { return userId; }
    public String getAuthor() { return author; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    // setters
    public void setId(String id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public void setCategory(String category) { this.category = category; }
    public void setReactionsCount(int count) { this.reactionsCount = count; }
}
