package nl.hva.election_backend.model;

import java.util.HashSet;
import java.util.Set;

public class Party {
    private String name;
    private Set<Candidate> candidates = new HashSet<>();

    public Party(String name) {
        this.name = name;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public String getName() {
        return name;
    }
}
