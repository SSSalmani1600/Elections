package nl.hva.election_backend.entity.id;

import java.util.Objects;

public class ConstituencyId {
    private int year;
    private String constituencyId;

    public ConstituencyId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConstituencyId that = (ConstituencyId) o;
        return year == that.year && Objects.equals(constituencyId, that.constituencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, constituencyId);
    }
}
