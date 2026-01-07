package nl.hva.election_backend.model;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(
        name = "poll_votes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"poll_id", "user_id"}
        )
)
public class PollVote {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poll_id")
    @JsonBackReference
    private Poll poll;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String choice;

    protected PollVote() {}

    public PollVote(Poll poll, Long userId, String choice) {
        this.poll = poll;
        this.userId = userId;
        this.choice = choice;
    }

    public UUID getId() { return id; }
    public Poll getPoll() { return poll; }
    public Long getUserId() { return userId; }
    public String getChoice() { return choice; }
}
