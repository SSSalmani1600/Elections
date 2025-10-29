package nl.hva.election_backend.model;

import java.util.HashMap;
import java.util.Map;

public class Candidate {
    private final String firstName;
    private final String lastName;
    private final String initials;
    private final String gender;
    private final String localityName;
    private final String electionName;
    private final String partiesId;
    private String shortCode = null;
    private boolean isElected = false;
    private String candidateId;
    private int votes;
    private final Map<String, Integer> votesByMunicipality = new HashMap<>();
    private final Map<String, Integer> votesByStation = new HashMap<>();

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
        return firstName + " " + lastName;
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

    public Map<String, Integer> getVotesByMunicipality() {
        return votesByMunicipality;
    }

    public Map<String, Integer> getVotesByStation() {
        return votesByStation;
    }
}