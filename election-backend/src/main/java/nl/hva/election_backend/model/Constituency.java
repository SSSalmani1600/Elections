package nl.hva.election_backend.model;

import java.util.HashSet;
import java.util.Set;

public class Constituency {
    private String name;
    private String constituencyId;
    private Set<Party> parties = new HashSet<>();

    public Constituency(String name, String constituencyId) {
        this.name = name;
        this.constituencyId = constituencyId;
    }

    public Constituency() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String contestId) {
        this.constituencyId = contestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constituency that)) return false;
        return name != null && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
