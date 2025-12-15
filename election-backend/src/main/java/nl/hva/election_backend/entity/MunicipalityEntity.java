package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.MunicipalityId;

import java.util.List;

@Entity
@IdClass(MunicipalityId.class)
@Table(name = "municipalities")
public class MunicipalityEntity {
    @Id
    @Column(name = "year")
    private int year;

    @Id
    @Column(name = "municipality_id")
    private String municipalityId;

    @Column(name = "constituency_id", nullable = false)
    private String constituencyId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "municipality", fetch = FetchType.LAZY)
    private List<MunicipalityResultEntity> results;

    public MunicipalityEntity(int year, String municipalityId, String constituencyId, String name) {
        this.year = year;
        this.municipalityId = municipalityId;
        this.constituencyId = constituencyId;
        this.name = name;
    }

    public MunicipalityEntity() {

    }

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

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MunicipalityResultEntity> getResults() {
        return results;
    }

    public void setResults(List<MunicipalityResultEntity> results) {
        this.results = results;
    }
}
