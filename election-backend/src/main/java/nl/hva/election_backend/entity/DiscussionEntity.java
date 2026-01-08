package nl.hva.election_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import nl.hva.election_backend.model.User;
import java.time.Instant;
import java.util.List;

// Database entiteit: representeert een discussie in de database
// Deze class wordt door JPA gebruikt om data op te slaan en op te halen
@Entity
@Table(name = "discussions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiscussionEntity {

    // Primaire sleutel: automatisch gegenereerd door de database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titel van de discussie (verplicht veld)
    @Column(nullable = false)
    @NotBlank(message = "Titel is verplicht")
    @Size(min = 5, max = 255, message = "Titel moet tussen de 5 en 255 karakters zijn")
    private String title;

    // Inhoud/body van de discussie (TEXT type voor lange teksten)
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Inhoud is verplicht")
    @Size(min = 10, message = "Inhoud moet minimaal 10 karakters bevatten")
    private String body;

    // Categorie van de discussie (optioneel)
    @Column
    private String category;

    // Relatie naar de gebruiker die de discussie heeft aangemaakt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    public User getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setCategory(String category) { this.category = category; }
    public void setUser(User user) { this.user = user; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public void setReactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; }
}
