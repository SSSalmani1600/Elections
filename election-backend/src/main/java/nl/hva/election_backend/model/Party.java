package nl.hva.election_backend.model;

import java.util.HashSet;
import java.util.Set;

public class Party {
    private String name;
    private String id;
    private int votes;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setCandidates(Set<Candidate> candidates) {
        this.candidates = candidates;
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
}
