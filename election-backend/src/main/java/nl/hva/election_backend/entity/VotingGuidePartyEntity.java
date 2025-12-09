package nl.hva.election_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "voting_guide_parties")
public class VotingGuidePartyEntity {
    @Id
    private Long id;
    private String name;

    public VotingGuidePartyEntity() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
