package nl.hva.election_backend.entity.id;

import java.util.Objects;

public class ConstituencyResultId {
    private int year;
    private String constituencyId;
    private String partyId;

    public ConstituencyResultId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConstituencyResultId that = (ConstituencyResultId) o;
        return year == that.year && Objects.equals(constituencyId, that.constituencyId) && Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, constituencyId, partyId);
    }
}
