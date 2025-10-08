package nl.hva.ict.sm3.backend.model;

import java.util.HashSet;
import java.util.Set;

/**
 * This will hold the information for one specific election.<br/>
 * <b>This class is by no means production ready! You need to alter it extensively!</b>
 */
public class Election {
    private final String id;
    private Set<Candidate> candidates = new HashSet<>();

    public Election(String id) {
        this.id = id;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return "You have to create a proper election model yourself!";
    }

    public String getId() {
        return id;
    }
}
