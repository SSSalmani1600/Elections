package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.PartyId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parties")
public class PartyEntity {

    @Id
    @Column(name = "party_id")
    private String partyId;

    @Column(nullable = false)
    private String name;

    @Column(name = "year")
    private int year;

    @OneToMany(mappedBy = "party")
    private Set<CandidateEntity> candidates = new HashSet<>();

    public PartyEntity() {}

    public PartyEntity(String name, int year, String partyId) {
        this.partyId = partyId;
        this.name = name;
        this.year = year;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PartyEntity that = (PartyEntity) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
