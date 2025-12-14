package nl.hva.election_backend.dto;

import java.util.List;

public class MunicipalityDto {
    private String municipalityId;
    private String name;
    private String constituencyId;
    private List<PartyResultDto> parties;

    public MunicipalityDto() {
    }

    public String getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(String municipalityId) {
        this.municipalityId = municipalityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public List<PartyResultDto> getParties() {
        return parties;
    }

    public void setParties(List<PartyResultDto> parties) {
        this.parties = parties;
    }
}
