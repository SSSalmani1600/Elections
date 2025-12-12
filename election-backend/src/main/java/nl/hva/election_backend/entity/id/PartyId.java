package nl.hva.election_backend.entity.id;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PartyId implements Serializable {

    private int year;
    private String partyId;

    public PartyId() {}

    public PartyId(int year, String partyId) {
        this.year = year;
        this.partyId = partyId;
    }

    public int getYear() {
        return year;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartyId)) return false;
        PartyId that = (PartyId) o;
        return year == that.year && Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, partyId);
    }
}
