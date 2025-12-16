package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "voting_guide_answers")
public class VotingGuideAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "statement_id", nullable = false)
    private Long statementId;

    @Column(nullable = false)
    private String answer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    public VotingGuideAnswerEntity() {}

    public VotingGuideAnswerEntity(Long userId, Long statementId, String answer) {
        this.userId = userId;
        this.statementId = statementId;
        this.answer = answer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getAnswer() {
        return answer;
    }

    public Long getStatementId() {
        return statementId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
