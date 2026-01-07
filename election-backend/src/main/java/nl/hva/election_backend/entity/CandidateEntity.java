package nl.hva.election_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class CandidateEntity {

    @Id
    @Column(name = "candidate_id", nullable = false)
    private String candidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "party_id", referencedColumnName = "party_id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "year", referencedColumnName = "year", nullable = false, insertable = false, updatable = false)
    })
    private PartyEntity party;

    @Column(name = "party_id")
    private String partyId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "name_prefix")
    private String namePrefix;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "initials")
    private String initials;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "year", insertable = true, updatable = true)
    private int year;

    public CandidateEntity() {}

    public String getCandidateId() {
        return candidateId;
    }

    public PartyEntity getParty() {
        return party;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public String getLastName() {
        return lastName;
    }

    public String getInitials() {
        return initials;
    }

    public String getGender() {
        return gender;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public int getYear() {
        return year;
    }
}
