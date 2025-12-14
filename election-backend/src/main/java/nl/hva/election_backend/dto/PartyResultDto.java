package nl.hva.election_backend.dto;

public class PartyResultDto {
    private String partyId;
    private String name;
    private int validVotes;

    public PartyResultDto(String partyId, String name, int validVotes) {
        this.partyId = partyId;
        this.name = name;
        this.validVotes = validVotes;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValidVotes() {
        return validVotes;
    }

    public void setValidVotes(int validVotes) {
        this.validVotes = validVotes;
    }
}
