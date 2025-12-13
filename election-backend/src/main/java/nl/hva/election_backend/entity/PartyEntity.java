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

    public String getPartyId() { return partyId; }
    public String getName() { return name; }
    public int getYear() { return year; }
}
