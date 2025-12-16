package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "voting_guide_results")
public class VotingGuideResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @Column(name = "party_name", nullable = false)
    private String partyName;

    @Column(nullable = false)
    private Long percentage;

    public VotingGuideResultEntity(Long userId, Long partyId, String partyName, Long percentage) {
        this.userId = userId;
        this.partyId = partyId;
        this.partyName = partyName;
        this.percentage = percentage;
    }

    public VotingGuideResultEntity() {

    }

    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPartyId() {
        return partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public Long getPercentage() {
        return percentage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }
}
