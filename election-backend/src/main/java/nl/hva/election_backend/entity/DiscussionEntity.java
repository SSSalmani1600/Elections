package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "discussions")
public class DiscussionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "category")
    private String category;

    // -------------------------
    // USER FK COLUMN (blijft bestaan)
    // -------------------------
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // -------------------------
    // USER RELATION (NIEUWE RELATIE)
    // -------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity user;
    // - insertable/updatable = false zodat userId gewoon gebruikt wordt bij save()
    // - Hibernate gebruikt dit alleen voor JOIN FETCH

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant lastActivityAt;

    @Column(name = "reactions_count", nullable = false)
    private int reactionsCount;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReactionEntity> reactions;

    public DiscussionEntity() {}

    public DiscussionEntity(Long id, String title, String body, String category, Long userId,
                            Instant createdAt, Instant lastActivityAt, int reactionsCount) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastActivityAt = lastActivityAt;
        this.reactionsCount = reactionsCount;
    }

    // -------------------------
    // GETTERS / SETTERS
    // -------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    //  BELANGRIJK: HIER WILDEN WE NAAR TOE
    public UserEntity getUser() { return user; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public int getReactionsCount() { return reactionsCount; }
    public void setReactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; }

    public List<ReactionEntity> getReactions() { return reactions; }
    public void setReactions(List<ReactionEntity> reactions) { this.reactions = reactions; }
}
