package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import nl.hva.election_backend.entity.id.PartyId;

@Entity
@IdClass(PartyId.class)
@Table(name = "parties")
public class PartyEntity {

    @Id
    private int year;

    @Id
    private String partyId;

    @Column(nullable = false)
    private String name;

    public PartyEntity() {}

    public PartyEntity(String name, int year, String partyId) {
        this.name = name;
        this.year = year;
        this.partyId = partyId;
    }

    public String getId() {
        return partyId;
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
