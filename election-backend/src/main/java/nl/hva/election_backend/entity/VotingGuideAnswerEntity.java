package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.model.User;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "voting_guide_answers")
public class VotingGuideAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private StatementEntity statement;

    @Column(nullable = false)
    private String answer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    public VotingGuideAnswerEntity() {}

    public VotingGuideAnswerEntity(User user, StatementEntity statement, String answer) {
        this.user = user;
        this.statement = statement;
        this.answer = answer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getAnswer() {
        return answer;
    }

    public StatementEntity getStatement() {
        return statement;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatement(StatementEntity statement) {
        this.statement = statement;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
