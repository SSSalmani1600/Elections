package nl.hva.election_backend.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This will hold the information for one specific election.<br/>
 * <b>This class is by no means production ready! You need to alter it extensively!</b>
 *
 */
public class Election {
    private String id;
    private final List<Party> parties = new ArrayList<>();
    private Set<Constituency> constituencies = new HashSet<>();
    private String type;
    private String date;
    private String status;

    public Election(String id, String type, String date, String status) {

        this.id = id;
        this.type = type;
        this.date = date;
        this.status = status;
    }

    public Election(String electionId) {
    }

    public String getType() { return type; }
    public String getDate() { return date; }
    public String getStatus() { return status; }

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

    public Set<Constituency> getConstituencies() {
        return constituencies;
    }
}
