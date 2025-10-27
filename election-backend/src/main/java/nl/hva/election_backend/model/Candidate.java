package nl.hva.election_backend.model;

public class Candidate {
    private String firstName;
    private String lastName;
    private String initials;
    private String gender;
    private String localityName;
    private String electionName;
    private String partiesId;
    private String candidateId;
    private int votes;

    public Candidate(String firstName, String lastName, String initials, String gender, String localityName, String electionName, String affiliationId, String candidateId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.initials = initials;
        this.gender = gender;
        this.localityName = localityName;
        this.electionName = electionName;
        this.partiesId = affiliationId;
        this.candidateId = candidateId;
    }

    public Candidate(Candidate copy) {
        this(
                copy.getFirstName(),
                copy.getLastName(),
                copy.getInitials(),
                copy.getGender(),
                copy.getLocalityName(),
                copy.getElectionName(),
                copy.getPartiesId(),
                copy.getCandidateId()
        );
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

    public String getElectionName() {
        return electionName;
    }

    public String getPartiesId() {
        return partiesId;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
}
