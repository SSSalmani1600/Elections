package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "party_statement_positions")
public class PartyViewpointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private VotingGuidePartyEntity party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private StatementEntity statement;

    @Column(name = "position", nullable = false)
    private String position;

    public PartyViewpointEntity() {
    }

    public PartyViewpointEntity(Long id, VotingGuidePartyEntity party, StatementEntity statement, String position) {
        this.id = id;
        this.party = party;
        this.statement = statement;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public VotingGuidePartyEntity getParty() {
        return party;
    }

    public StatementEntity getStatement() {
        return statement;
    }

    public String getPosition() {
        return position;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParty(VotingGuidePartyEntity party) {
        this.party = party;
    }

    public void setStatement(StatementEntity statement) {
        this.statement = statement;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyViewpointEntity that = (PartyViewpointEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
