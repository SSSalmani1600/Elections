package nl.hva.election_backend.dto;

public class VotingGuideResultDto {
    private Number partyId;
    private String partyName;
    private Number percentage;

    public VotingGuideResultDto(Number partyId, String partyName, Number percentage) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.percentage = percentage;
    }

    public Number getPartyId() {
        return partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public Number getPercentage() {
        return percentage;
    }

    public void setPartyId(Number partyId) {
        this.partyId = partyId;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPercentage(Number percentage) {
        this.percentage = percentage;
    }
}
