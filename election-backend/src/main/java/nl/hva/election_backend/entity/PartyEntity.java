package nl.hva.election_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parties")
public class PartyEntity {

    @Id
    private String party_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int year;

    public PartyEntity() {}

    public PartyEntity(String name, int year) {
        this.name = name;
        this.year = year;
        this.party_id = generatePartyId(name, year);
    }

    private String generatePartyId(String name, int year) {
        return (name.toUpperCase()
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                + "_" + year);
    }

    public String getId() { return party_id; }
    public String getName() { return name; }
    public int getYear() { return year; }

    public void setName(String name) { this.name = name; }
    public void setYear(int year) { this.year = year; }
}
