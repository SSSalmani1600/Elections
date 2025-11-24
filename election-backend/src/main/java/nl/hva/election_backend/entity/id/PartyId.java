package nl.hva.election_backend.entity.id;

import java.util.Objects;

public class PartyId {
    private int year;
    private String partyId;

    public PartyId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PartyId partyId1 = (PartyId) o;
        return year == partyId1.year && Objects.equals(partyId, partyId1.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, partyId);
    }
}
