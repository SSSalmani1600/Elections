package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.ConstituencyId;

@Entity
@IdClass(ConstituencyId.class)
@Table(name = "constituencies")
public class ConstituencyEntity {

    @Id
    @Column(name = "constituency_id")
    private String constituencyId;

    @Id
    private int year;

    @Column(nullable = false)
    private String name;

    public ConstituencyEntity(String constituencyId, int year, String name) {
        this.constituencyId = constituencyId;
        this.year = year;
        this.name = name;
    }

    public ConstituencyEntity() {
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}