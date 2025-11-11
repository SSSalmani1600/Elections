package nl.hva.election_backend.model;

public class Candidate {
    private final String firstName;
    private final String lastName;
    private final String namePrefix;
    private final String initials;
    private final String gender;
    private final String localityName;
    private final String electionName;
    private final String partiesId;
    private String shortCode = null;
    private boolean isElected = false;
    private String candidateId;
    private int votes;

    public Candidate(String firstName, String namePrefix, String lastName, String initials, String gender, String localityName, String electionName, String affiliationId, String candidateId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.namePrefix = namePrefix;
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
                copy.getNamePrefix(),
                copy.getLastName(),
                copy.getInitials(),
                copy.getGender(),
                copy.getLocalityName(),
                copy.getElectionName(),
                copy.getPartiesId(),
                copy.getCandidateId()
        );
    }


    public boolean isElected() {
        return isElected;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setElected(boolean elected) {
        isElected = elected;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getNamePrefix() {return namePrefix;}

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

    public String getNameLine() {
        return null;
    }

    public String getFullName() {
        if (namePrefix == null || namePrefix.isBlank()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + namePrefix + " " + lastName;
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
