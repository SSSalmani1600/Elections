package nl.hva.election_backend.entity.id;

import java.util.Objects;

public class MunicipalityResultId {
    private int year;
    private String municipalityId;
    private String partyId;

    public MunicipalityResultId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MunicipalityResultId that = (MunicipalityResultId) o;
        return year == that.year && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, municipalityId, partyId);
    }
}
