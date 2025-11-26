package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.ConstituencyResultId;

@Entity
@IdClass(ConstituencyResultId.class)
@Table(name = "constituency_results")
public class ConstituencyResultEntity {
    @Id
    private int year;

    @Id
    @Column(name = "constituency_id")
    private String constituencyId;

    @Id
    @Column(name = "party_id")
    private String partyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "year",
                    referencedColumnName = "year",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "constituency_id",
                    referencedColumnName = "constituency_id",
                    insertable = false,
                    updatable = false
            )
    })
    private ConstituencyEntity constituency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "year",
                    referencedColumnName = "year",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "party_id",
                    referencedColumnName = "party_id",
                    insertable = false,
                    updatable = false
            )
    })
    private PartyEntity party;

    @Column(name = "valid_votes")
    private int validVotes;

    public ConstituencyResultEntity(int year, String constituencyId, String partyId, int validVotes) {
        this.year = year;
        this.constituencyId = constituencyId;
        this.partyId = partyId;
        this.validVotes = validVotes;
    }

    public ConstituencyResultEntity() {

    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public int getValidVotes() {
        return validVotes;
    }

    public void setValidVotes(int validVotes) {
        this.validVotes = validVotes;
    }

    public ConstituencyEntity getConstituency() {
        return constituency;
    }

    public void setConstituency(ConstituencyEntity constituency) {
        this.constituency = constituency;
    }

    public PartyEntity getParty() {
        return party;
    }

    public void setParty(PartyEntity party) {
        this.party = party;
    }
}
