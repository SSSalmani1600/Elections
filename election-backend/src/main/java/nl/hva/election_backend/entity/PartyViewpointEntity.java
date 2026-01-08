package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "party_statement_positions")
public class PartyViewpointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "party_id")
    private Long partyId;
    @Column(name = "statement_id")
    private Long statementId;
    @Column(name = "position", nullable = false)
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
