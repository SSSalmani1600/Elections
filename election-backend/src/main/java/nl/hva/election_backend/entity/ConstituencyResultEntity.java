package nl.hva.election_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import nl.hva.election_backend.entity.id.ConstituencyResultId;

@Entity
@IdClass(ConstituencyResultId.class)
@Table(name = "constituency_results")
public class ConstituencyResultEntity {
    @Id
    private int year;

    @Id
    private String constituencyId;

    @Id
    private String partyId;

    public ConstituencyResultEntity(int year, String constituencyId, String partyId) {
        this.year = year;
        this.constituencyId = constituencyId;
        this.partyId = partyId;
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
}
