package nl.hva.ict.sm3.backend.model;

public class Candidate {
    private String firstName;
    private String lastName;
    private String initials;
    private String gender;
    private String localityName;
    private String electionId;
    private String electionName;
    private int affiliationId;

    public Candidate(String firstName, String lastName, String initials, String gender, String localityName, String electionId, String electionName, int affiliationId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.initials = initials;
        this.gender = gender;
        this.localityName = localityName;
        this.electionId = electionId;
        this.electionName = electionName;
        this.affiliationId = affiliationId;
    }

    public String getFirstName() {
        return firstName;
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

    public String getLocalityName() {
        return localityName;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getElectionName() {
        return electionName;
    }

    public int getAffiliationId() {
        return affiliationId;
    }
}
