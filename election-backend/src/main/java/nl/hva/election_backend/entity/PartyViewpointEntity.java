package nl.hva.election_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "party_statement_positions")
public class PartyViewpointEntity {
    @Id
    private Long id;
    @Column(name = "party_id")
    private Long partyId;
    @Column(name = "statement_id")
    private Long statementId;
    private String position;

    public PartyViewpointEntity() {
    }

    public PartyViewpointEntity(Long id, Long partyId, Long statementId, String position) {
        this.id = id;
        this.partyId = partyId;
        this.statementId = statementId;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Long getPartyId() {
        return partyId;
    }

    public Long getStatementId() {
        return statementId;
    }

    public String getPosition() {
        return position;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PartyEntity that = (PartyEntity) o;

        return statementId.equals(that.getPartyId());
    }
}