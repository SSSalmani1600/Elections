package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.model.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private VotingGuidePartyEntity party;

    @Column(name = "party_name", nullable = false)
    private String partyName;

    @Column(nullable = false)
    private Long percentage;

    public VotingGuideResultEntity(User user, VotingGuidePartyEntity party, String partyName, Long percentage) {
        this.user = user;
        this.party = party;
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

    public User getUser() {
        return user;
    }

    public VotingGuidePartyEntity getParty() {
        return party;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setParty(VotingGuidePartyEntity party) {
        this.party = party;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }
}
