package nl.hva.election_backend.model;

import java.util.HashSet;
import java.util.Set;

public class Party {
    private String name;
    private final Set<Candidate> candidates = new HashSet<>();
    private Set<ElectedCandidates> electedCandidates = new HashSet<>();
    public Party(String name) {
        this.name = name;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public String getName() {
        return name;
    }

    public void setVotes(int votes) {
    }

    public Set<ElectedCandidates> getElectedCandidates() {
        return electedCandidates;
    }

    public void setElectedCandidates(Set<ElectedCandidates> electedCandidates) {
        this.electedCandidates = electedCandidates;
    }
}
