package nl.hva.election_backend.dto;

import java.time.Instant;

public class ReactionDto {
    private Long id;        // handig voor debugging / frontend
    private String author;  // naam van de user
    private String message;
    private Instant createdAt;

    // ✅ Lege constructor (nodig voor JSON)
    public ReactionDto() {}

    // ✅ Constructor die service gebruikt
    public ReactionDto(String author, String message, Instant createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // ✅ Optioneel: constructor met id erbij, handig bij return uit DB
    public ReactionDto(Long id, String author, String message, Instant createdAt) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

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
