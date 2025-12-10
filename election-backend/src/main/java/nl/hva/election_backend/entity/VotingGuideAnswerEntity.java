package nl.hva.election_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class VotingGuideAnswerEntity {
    @Id
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "statement_id")
    private Long statementId;
    private String answer;
    @Column(name = "created_at")
    private Date createdAt;

    public VotingGuideAnswerEntity() {}

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getStatementId() {
        return statementId;
    }

    public String getAnswer() {
        return answer;
    }

    public Date getCreatedAt() {
        return createdAt;
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
