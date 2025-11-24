package nl.hva.election_backend.entity.id;

import java.util.Objects;

public class MunicipalityId {
    private int year;
    private String municipalityId;

    public MunicipalityId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MunicipalityId that = (MunicipalityId) o;
        return year == that.year && Objects.equals(municipalityId, that.municipalityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, municipalityId);
    }
}
