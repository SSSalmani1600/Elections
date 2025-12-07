package nl.hva.election_backend.dto;

public class VotingGuideResultDto {
    private Long partyId;
    private String partyName;
    private double percentage;

    public VotingGuideResultDto(Long partyId, String partyName, double percentage) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.percentage = percentage;
    }

    public Long getPartyId() {
        return partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
