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

//    private String generatePartyId(String name, int year) {
//        return (name.toUpperCase()
//                .replaceAll("[^A-Z0-9]+", "_")
//                .replaceAll("_+", "_")
//                .replaceAll("^_|_$", "")
//                + "_" + year);
//    }

    public String getId() { return partyId; }
    public String getName() { return name; }
    public int getYear() { return year; }

    public void setName(String name) { this.name = name; }
    public void setYear(int year) { this.year = year; }
}
