package nl.hva.election_backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue
    private UUID id;

    private String question;

    public Poll() {}

    public Poll(String question) {
        this.question = question;
    }

    public UUID getId() { return id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
