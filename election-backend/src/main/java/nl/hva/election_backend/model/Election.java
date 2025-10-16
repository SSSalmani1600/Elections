package nl.hva.election_backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This will hold the information for one specific election.<br/>
 * <b>This class is by no means production ready! You need to alter it extensively!</b>
 */
public class Election {
    private final String id;
    private final List<Party> parties = new ArrayList<>();

    public Election(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "You have to create a proper election model yourself!";
    }

    public String getId() {
        return id;
    }

    public List<Party> getParties() {
        return parties;
    }

    public Party getPartyByName(String affiliationId) {
        return parties.stream().filter(aff -> aff.getName().equals(affiliationId)).findFirst().orElse(null);
    }

    public Candidate getCandidateByName(String fullName) {
        return parties.stream()
                .flatMap(party -> party.getCandidates().stream())
                .filter(candidate -> candidate.getFullName().equalsIgnoreCase(fullName))
                .findFirst()
                .orElse(null);
    }

}
