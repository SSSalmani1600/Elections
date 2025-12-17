package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

// Database entiteit: representeert een discussie in de database
// Deze class wordt door JPA gebruikt om data op te slaan en op te halen
@Entity
@Table(name = "discussions")
public class DiscussionEntity {

    // Primaire sleutel: automatisch gegenereerd door de database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titel van de discussie (verplicht veld)
    @Column(nullable = false)
    private String title;

    // Inhoud/body van de discussie (TEXT type voor lange teksten)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    // Categorie van de discussie (optioneel)
    @Column
    private String category;

    // ID van de gebruiker die de discussie heeft aangemaakt
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Relatie naar UserEntity: LAZY betekent dat user data pas wordt opgehaald als je het nodig hebt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private UserEntity user;

    // Wanneer de discussie is aangemaakt
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    // Wanneer er voor het laatst activiteit was (bijv. nieuwe reactie)
    @Column(name = "last_activity_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant lastActivityAt;

    // Aantal reacties op deze discussie
    @Column(name = "reactions_count", nullable = false)
    private int reactionsCount;

    // Relatie naar alle reacties: OneToMany betekent 1 discussie heeft meerdere reacties
    // cascade = ALL betekent dat als je een discussie verwijdert, worden reacties ook verwijderd
    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionEntity> reactions;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCategory() { return category; }
    public Long getUserId() { return userId; }
    public UserEntity getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setCategory(String category) { this.category = category; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public void setReactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; }
}
