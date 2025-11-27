package nl.hva.election_backend.dto;

import java.time.Instant;

// DTO voor een reactie: wordt naar de frontend gestuurd
// Bevat alleen de data die nodig is voor weergave
public class ReactionDto {
    private Long id;                    // Unieke ID van de reactie
    private String username;            // Username van de auteur
    private String message;             // Inhoud van de reactie
    private Instant createdAt;          // Wanneer de reactie is aangemaakt

    public ReactionDto() {}

    // Constructor: maakt een nieuwe ReactionDto aan
    public ReactionDto(Long id, String username, String message, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Voor backwards compatibility: frontend verwacht soms "author" in plaats van "username"
    public String getAuthor() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
