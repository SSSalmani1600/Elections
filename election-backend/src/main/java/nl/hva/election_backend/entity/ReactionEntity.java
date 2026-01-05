package nl.hva.election_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import nl.hva.election_backend.model.User;
import java.time.Instant;

// Database entiteit: representeert een reactie op een discussie
@Entity
@Table(name = "reactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReactionEntity {

    // Primaire sleutel: automatisch gegenereerd door de database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relatie naar de discussie waar deze reactie bij hoort
    // @JsonIgnore: wordt niet meegestuurd in JSON responses (voorkomt oneindige loops)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    private DiscussionEntity discussion;

    // Relatie naar de gebruiker die de reactie heeft geschreven
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // De inhoud van de reactie (TEXT type voor lange teksten)
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // Moderatiestatus: PENDING (wacht op goedkeuring), APPROVED (goedgekeurd), REJECTED (afgekeurd)
    @Column(name = "moderation_status", nullable = false)
    private String moderationStatus = "PENDING";

    // Reden waarom de reactie is afgekeurd (optioneel)
    @Column(name = "flagged_reason")
    private String flaggedReason;

    // Wanneer de reactie is aangemaakt
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt = Instant.now();

    public ReactionEntity() {}

    public ReactionEntity(DiscussionEntity discussion, User user, String message) {
        this.discussion = discussion;
        this.user = user;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DiscussionEntity getDiscussion() { return discussion; }
    public void setDiscussion(DiscussionEntity discussion) { this.discussion = discussion; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getModerationStatus() { return moderationStatus; }
    public void setModerationStatus(String moderationStatus) { this.moderationStatus = moderationStatus; }

    public String getFlaggedReason() { return flaggedReason; }
    public void setFlaggedReason(String flaggedReason) { this.flaggedReason = flaggedReason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
