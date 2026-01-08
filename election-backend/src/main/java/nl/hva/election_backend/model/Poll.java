package nl.hva.election_backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String question;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<PollVote> votes = new ArrayList<>();

    protected Poll() {}

    public Poll(String question) {
        this.question = question;
    }

    public UUID getId() { return id; }
    public String getQuestion() { return question; }
    public Instant getCreatedAt() { return createdAt; }
    public List<PollVote> getVotes() { return votes; }
}
