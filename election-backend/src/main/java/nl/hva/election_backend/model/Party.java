package nl.hva.election_backend.model;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class Party {
    private String name;
    private String id;
    private int votes;
    private Set<Candidate> candidates = new HashSet<>();
    private Set<ElectedCandidates> electedCandidates = new HashSet<>();
    private final Map<String, Integer> votesByMunicipality = new HashMap<>();
    private final Map<String, Integer> votesByStation = new HashMap<>();

    public Party(String name) {
        this.name = name;
    }

    public Party(String partyId, String name, int votes) {
        this.id = partyId;
        this.name = name;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Set<ElectedCandidates> getElectedCandidates() {
        return electedCandidates;
    }

    public void setElectedCandidates(Set<ElectedCandidates> electedCandidates) {
        this.electedCandidates = electedCandidates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCandidates(Set<Candidate> candidates) {
        this.candidates = candidates;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Party that)) return false;
        return name != null && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public Map<String, Integer> getVotesByMunicipality() {
        return votesByMunicipality;
    }

    public Map<String, Integer> getVotesByStation() {
        return votesByStation;
    }
}