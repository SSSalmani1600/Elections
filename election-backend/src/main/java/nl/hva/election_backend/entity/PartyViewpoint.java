package nl.hva.election_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PartyViewpoint {
    @Id
    private Long id;
    @Column(name = "party_id")
    private Long partyId;
    @Column(name = "statement_id")
    private Long statementId;
    private String position;

    public PartyViewpoint(){}

    public PartyViewpoint(Long id, Long partyId, Long statementId, String position) {
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
}
