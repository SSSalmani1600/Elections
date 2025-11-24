package nl.hva.election_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import nl.hva.election_backend.entity.id.MunicipalityResultId;

@Entity
@IdClass(MunicipalityResultId.class)
@Table(name = "municipality_results")
public class MunicipalityResultEntity {
    @Id
    private int year;

    @Id
    private String municipalityId;

    @Id
    private String partyId;

    private int validVotes;

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
}
