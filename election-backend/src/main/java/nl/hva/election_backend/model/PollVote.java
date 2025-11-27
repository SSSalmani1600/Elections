package nl.hva.election_backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "poll_votes")
public class PollVote {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID pollId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String choice;

    public PollVote() {}

    public PollVote(UUID pollId, Long userId, String choice) {
        this.pollId = pollId;
        this.userId = userId;
        this.choice = choice;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPollId() {
        return pollId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getChoice() {
        return choice;
    }
}
