package nl.hva.election_backend.dto;

import java.time.Instant;

public class ReactionDto {
    private Long id;
    private String username;
    private String message;
    private Instant createdAt;

    public ReactionDto(Long id, String username, String message, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
