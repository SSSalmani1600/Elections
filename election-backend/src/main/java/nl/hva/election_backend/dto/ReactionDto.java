package nl.hva.election_backend.dto;

import java.time.Instant;

public class ReactionDto {
    private Long id;
    private Long userId;      // ID van de gebruiker (voor edit/delete check)
    private String author;    // naam van de user
    private String message;
    private Instant createdAt;

    public ReactionDto() {}

    public ReactionDto(Long id, Long userId, String author, String message, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAuthor() { return author; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAuthor(String author) { this.author = author; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
