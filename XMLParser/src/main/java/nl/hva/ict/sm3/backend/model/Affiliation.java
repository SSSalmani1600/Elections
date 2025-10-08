package nl.hva.ict.sm3.backend.model;

import java.util.HashSet;
import java.util.Set;

public class Affiliation {
    private String name;
    private Set<Candidate> candidates = new HashSet<>();

    public Affiliation(String name) {
        this.name = name;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public String getName() {
        return name;
    }
}
