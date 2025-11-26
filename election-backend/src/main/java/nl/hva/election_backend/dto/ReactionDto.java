package nl.hva.election_backend.dto;

import java.time.Instant;

public class ReactionDto {
    private Long id;
    private String username;
    private String message;
    private Instant createdAt;

    public ReactionDto() {}

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

    // For backwards compatibility with frontend expecting "author"
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
