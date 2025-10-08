package nl.hva.ict.sm3.backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This will hold the information for one specific election.<br/>
 * <b>This class is by no means production ready! You need to alter it extensively!</b>
 */
public class Election {
    private final String id;
    private List<Affiliation> affiliations = new ArrayList<>();

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

    public List<Affiliation> getAffiliations() {
        return affiliations;
    }
}
