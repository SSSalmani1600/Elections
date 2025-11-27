package nl.hva.election_backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue
    private UUID id;

    private String question;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public Poll() {}

    public Poll(String question) {
        this.question = question;
    }

    public UUID getId() { return id; }
    public String getQuestion() { return question; }
    public Instant getCreatedAt() { return createdAt; }

    public void setQuestion(String question) { this.question = question; }
}
