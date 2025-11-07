package nl.hva.election_backend.dto;

import java.time.Instant;

public class ReactionDto {
    private String author;
    private String message;
    private Instant createdAt;

    // ✅ Lege constructor nodig voor JSON (Spring)
    public ReactionDto() {}

    // ✅ Handige constructor voor aanmaken
    public ReactionDto(String author, String message, Instant createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // ✅ Getters
    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // ✅ Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
