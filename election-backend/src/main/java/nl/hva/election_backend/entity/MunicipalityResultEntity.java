package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.MunicipalityResultId;

@Entity
@IdClass(MunicipalityResultId.class)
@Table(name = "municipality_results")
public class MunicipalityResultEntity {
    @Id
    @Column(name = "year")
    private int year;

    @Id
    @Column(name = "municipality_id")
    private String municipalityId;

    @Id
    @Column(name = "party_id")
    private String partyId;

    private int validVotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "municipality_id", referencedColumnName = "municipality_id", insertable = false, updatable = false),
            @JoinColumn(name = "year", referencedColumnName = "year", insertable = false, updatable = false)
    })
    private MunicipalityEntity municipality;

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

    public MunicipalityResultEntity(int year, String municipalityId, String partyId, int validVotes) {
        this.year = year;
        this.municipalityId = municipalityId;
        this.partyId = partyId;
        this.validVotes = validVotes;
    }

    public MunicipalityResultEntity() {}

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(String municipalityId) {
        this.municipalityId = municipalityId;
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

    public MunicipalityEntity getMunicipality() {
        return municipality;
    }

    public void setMunicipality(MunicipalityEntity municipality) {
        this.municipality = municipality;
    }

    public PartyEntity getParty() {
        return party;
    }

    public void setParty(PartyEntity party) {
        this.party = party;
    }
}
