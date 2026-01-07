package nl.hva.election_backend.dto;

public class PartyResultDto {
    private String partyId;
    private String name;
    private int votes;

    public PartyResultDto(String partyId, String name, int votes) {
        this.partyId = partyId;
        this.name = name;
        this.votes = votes;
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

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}